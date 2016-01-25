//
//  QuestionsAndAnswersViewController.m
//  Smart Community
//
//  Created by Jukk on 8/17/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "QuestionsAndAnswersViewController.h"
#import "QuestionCommentsViewController.h"
#import "QuestionCell.h"
#import "LogicServices.h"
#import "CreateQuestionViewController.h"
#import "QuestionModel.h"

@interface QuestionsAndAnswersViewController ()

@end

@implementation QuestionsAndAnswersViewController
{
    UISearchBar *searchBar;
    BOOL _tableIsShowingPrivate;
    NSMutableArray* publicQuestionsArray;
    NSMutableArray* privateQuestionsArray;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    NSString* data;

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(PostQuestionSuccess:)
                                                 name:@"PostQuestionSuccess"
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(PostQuestionFail)
                                                 name:@"PostQuestionFail"
                                               object:data];
    
    [self reloadData];
    
        _tableIsShowingPrivate = NO;
        [self publicButtonClicked];
    [self appLanguageChange];
}

-(void) appLanguageChange {
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
        [_tabTitle setText:@"Q&A"];
        [_privateTitle setText:@"PRIVATE"];
        [_publicTitle setText:@"PUBLIC"];
    }
    if ([language isEqualToString:@"hebrew"]) {
        [_tabTitle setText:@"שאלות ותשובות"];
        [_privateTitle setText:@"פרטי"];
        [_publicTitle setText:@"ציבורי"];
    }
}


-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self reloadData];
}

-(void)reloadData {
    publicQuestionsArray = [NSMutableArray arrayWithArray:[[LogicServices sharedInstance] getAllCommunityPublicQuestions]];
    privateQuestionsArray = [NSMutableArray arrayWithArray:[[LogicServices sharedInstance] getAllCommunityPrivateQuestions]];
    
    publicQuestionsArray = [NSMutableArray arrayWithArray:[publicQuestionsArray sortedArrayUsingComparator: ^(postModel *d1, postModel *d2) {
        return [[[NSDate alloc]initWithTimeIntervalSince1970:d2.cDate/1000] compare:[[NSDate alloc]initWithTimeIntervalSince1970:d1.cDate/1000]];
    }]];
    privateQuestionsArray = [NSMutableArray arrayWithArray:[privateQuestionsArray sortedArrayUsingComparator: ^(postModel *d1, postModel *d2) {
        return [[[NSDate alloc]initWithTimeIntervalSince1970:d2.cDate/1000] compare:[[NSDate alloc]initWithTimeIntervalSince1970:d1.cDate/1000]];
    }]];
    
    [_questionsTable reloadData];
}

-(void)PostQuestionSuccess:(NSNotification *)notification {
    userModel* user = [DataManagement sharedInstance].user;
    postModel* post = [[postModel alloc]init];
    QuestionModel* question = notification.object;
    post.postID = question.questionID;
    post.communityMember = [[CommunityMemberModel alloc]init];
    post.communityMember.name = user.name;
    post.communityMember.imageUrl = user.imageUrl;
    post.title = question.title;
    post.cDate = [[NSDate date] timeIntervalSince1970]*1000;
    post.content = question.descr;
    post.isPublic = question.isPublic;
    if (!post.isPublic) {
        [[LogicServices sharedInstance] addCommunityPrivateQuestion:post];
        [privateQuestionsArray addObject:post];
        privateQuestionsArray = [NSMutableArray arrayWithArray:[privateQuestionsArray sortedArrayUsingComparator: ^(postModel *d1, postModel *d2) {
            return [[[NSDate alloc]initWithTimeIntervalSince1970:d2.cDate/1000] compare:[[NSDate alloc]initWithTimeIntervalSince1970:d1.cDate/1000]];
        }]];
    }
    else {
        [[LogicServices sharedInstance] addCommunityPublicQuestion:post];
        [publicQuestionsArray addObject:post];
        publicQuestionsArray = [NSMutableArray arrayWithArray:[publicQuestionsArray sortedArrayUsingComparator: ^(postModel *d1, postModel *d2) {
            return [[[NSDate alloc]initWithTimeIntervalSince1970:d2.cDate/1000] compare:[[NSDate alloc]initWithTimeIntervalSince1970:d1.cDate/1000]];
        }]];
    }

    [_questionsTable reloadData];
}

-(void)PostQuestionFail {
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Post Question Fail" message:@"Something went wrong.. try to send again" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
}

- (IBAction)privateButtonClicked {
    _tableIsShowingPrivate = YES;
    [_privateTitle setAlpha:1];
    [_privateUnderline setHidden:NO];
    [_publicTitle setAlpha:0.5];
    [_publicUnderline setHidden:YES];
    
    [_questionsTable reloadData];
}

- (IBAction)publicButtonClicked {
    _tableIsShowingPrivate = NO;
    [_privateTitle setAlpha:0.5];
    [_privateUnderline setHidden:YES];
    [_publicTitle setAlpha:1];
    [_publicUnderline setHidden:NO];
    
    [_questionsTable reloadData];
}

- (IBAction)createQuestionClicked {
    if ([[[LogicServices sharedInstance] getCurrentCommunity].memberType isEqualToString:@"guest"]) {
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Guest User" message:@"As a guest, you can't post questions" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alert show];
    }
    else {
        CreateQuestionViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"CreateQuestionViewController"];
        [self presentViewController:controller animated:YES completion:nil];
    }
}

- (IBAction)backAction:(id)sender
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
    if (_tableIsShowingPrivate) {
       return privateQuestionsArray.count;
    }
    else {
        return publicQuestionsArray.count;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 193;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 40;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QuestionCell *cell;
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"QuestionCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
    if (_tableIsShowingPrivate) {
        [cell initWithQuestion:privateQuestionsArray[indexPath.row]];
    }
    else {
        [cell initWithQuestion:publicQuestionsArray[indexPath.row]];
    }
    
    return cell;
}

/*- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    searchBar = [[UISearchBar alloc]initWithFrame:CGRectMake(0, 0, tableView.frame.size.width, 40)];
    searchBar.placeholder = @"Search";
    return searchBar;
}
*/

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    QuestionCommentsViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"QuestionCommentsViewController"];
    
    if (_tableIsShowingPrivate) {
        postModel* question = privateQuestionsArray[indexPath.row];
        controller.commentsArray = question.comments;
        controller.post = question;
        controller.isPublic = NO;
    }
    else {
        postModel* question = publicQuestionsArray[indexPath.row];
        controller.commentsArray = question.comments;
        controller.post = question;
        controller.isPublic = YES;
    }
    [self presentViewController:controller animated:YES completion:nil];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [searchBar resignFirstResponder];
}


@end
