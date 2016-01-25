//
//  CommunityConversationsViewController.m
//  Smart Community
//
//  Created by oren shany on 9/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "CommunityConversationsViewController.h"
#import "CommunityPostCell.h"
#import "QuestionCommentsViewController.h"
#import "LogicServices.h"
#import "CommunityModel.h"
#import "CreateQuestionViewController.h"

@interface CommunityConversationsViewController ()

@end

@implementation CommunityConversationsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    NSString* data;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(PostSuccess:)
                                                 name:@"PostSuccess"
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(PostFail)
                                                 name:@"PostFail"
                                               object:data];
    
    _table.delegate = self;
    _table.dataSource = self;
    _postsArray = [[LogicServices sharedInstance]getCurrentCommunity].communityPosts;
    _postsArray = [NSMutableArray arrayWithArray:[_postsArray sortedArrayUsingComparator: ^(postModel *d1, postModel *d2) {
        return [[[NSDate alloc]initWithTimeIntervalSince1970:d2.cDate/1000] compare:[[NSDate alloc]initWithTimeIntervalSince1970:d1.cDate/1000]];
    }]];
    if (_postsArray.count != 0) {
        [_table reloadData];
    }
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSString* appLanguage = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];

    if ([appLanguage isEqualToString:@"hebrew"]) {
        [_titleLabel setText:@"שיח קהילתי"];
    }
}

-(void)PostSuccess:(NSNotification *)notification {
    postModel* post = notification.object;
    [[LogicServices sharedInstance] addCommunityPost:post];
       [_postsArray addObject:post];
        _postsArray = [NSMutableArray arrayWithArray:[_postsArray sortedArrayUsingComparator: ^(postModel *d1, postModel *d2) {
            return [[[NSDate alloc]initWithTimeIntervalSince1970:d2.cDate/1000] compare:[[NSDate alloc]initWithTimeIntervalSince1970:d1.cDate/1000]];
        }]];
    
    [_table reloadData];
}

-(void)PostFail {
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Post Fail" message:@"Something went wrong.. try to send again" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _postsArray.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CommunityPostCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CommunityPostCell"];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"CommunityPostCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    postModel *post = [_postsArray objectAtIndex:indexPath.row];
    [cell initWithPost:post];
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    QuestionCommentsViewController * commentsController  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"QuestionCommentsViewController"];
    
    commentsController.fromConversationController = YES;
    postModel *post = [_postsArray objectAtIndex:indexPath.row];
    commentsController.commentsArray = [NSMutableArray arrayWithArray:post.comments];
    commentsController.post = post;
    [self presentViewController:commentsController animated:YES completion:nil];
}

- (IBAction)createNewPostClicked {
    if ([[[LogicServices sharedInstance] getCurrentCommunity].memberType isEqualToString:@"guest"]) {
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Guest User" message:@"As a guest, you can't post discussions" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alert show];
    }
    else {
        CreateQuestionViewController * commentsController  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"CreateQuestionViewController"];
        commentsController.fromConversations = YES;
        [self presentViewController:commentsController animated:YES completion:nil];
    }
}

- (IBAction)backButtonClicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}
@end
