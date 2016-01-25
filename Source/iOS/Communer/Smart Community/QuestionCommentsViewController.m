//
//  QuestionCommentsViewController.m
//  Smart Community
//
//  Created by oren shany on 9/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "QuestionCommentsViewController.h"
#import "QuestionCommentCellTableViewCell.h"
#import "CommentModel.h"
#import "Networking.h"
#import "DataManagement.h"
#import "LogicServices.h"
#import "UIImageView+AFNetworking.h"

@interface QuestionCommentsViewController ()

@end

@implementation QuestionCommentsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    NSString* data;
    [_questionTitle setText:_post.title];
    [_questionImage setImageWithURL:[NSURL URLWithString:_post.imageUrl] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
    [_questionImage.layer setCornerRadius:20.0f];
    [_questionImage.layer setMasksToBounds:YES];
    [_questionContent setText:_post.content];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
    NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
    [dateFormatter setTimeZone:tz];
    NSString *formattedDateString = [dateFormatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:_post.cDate/1000] ];
    [_questionDate setText:formattedDateString];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(PostQuestionCommentSuccess:)
                                                 name:@"PostQuestionCommentSuccess"
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(PostQuestionCommentFail)
                                                 name:@"PostQuestionCommentFail"
                                               object:data];

    
    [_containerView.layer setCornerRadius:10];
    [_containerView.layer setMasksToBounds:YES];
    
    _commentsArray = [NSMutableArray arrayWithArray:[_commentsArray sortedArrayUsingComparator: ^(CommentModel *d1, CommentModel *d2) {
        return [[[NSDate alloc]initWithTimeIntervalSince1970:d2.date/1000]  compare:[[NSDate alloc]initWithTimeIntervalSince1970:d1.date/1000] ];
    }]];
    
    _commentsTable.delegate = self;
    _commentsTable.dataSource = self;
}

-(void)viewWillAppear:(BOOL)animated {
    if (_hideTable) {
        [_commentsTable setHidden:YES];
    }
    
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"hebrew"]) {
        [_sendButton setTitle:@"שלח" forState:UIControlStateNormal];
    }
}

-(void)PostQuestionCommentSuccess:(NSNotification *)notification {
    
    if (_isPublic) {
        [[LogicServices sharedInstance] addCommunityPublicQuestionComment:notification.object ForQuestionID:_post.postID];
    }
    else {
        [[LogicServices sharedInstance] addCommunityPrivateQuestionComment:notification.object ForQuestionID:_post.postID];
    }
    
    [_commentsArray addObject:notification.object];
    _commentsArray = [NSMutableArray arrayWithArray:[_commentsArray sortedArrayUsingComparator: ^(CommentModel *d1, CommentModel *d2) {
        return [[[NSDate alloc]initWithTimeIntervalSince1970:d2.date/1000]  compare:[[NSDate alloc]initWithTimeIntervalSince1970:d1.date/1000] ];
    }]];
    [_commentsTable reloadData];
}

-(void)PostQuestionCommentFail {
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Post Comment Fail" message:@"Something went wrong.. try to send again" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];

}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
        return _commentsArray.count;
  }

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    QuestionCommentCellTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"QuestionCommentCellTableViewCell"];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"QuestionCommentCellTableViewCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    
   
        CommentModel *tempComment = [_commentsArray objectAtIndex:indexPath.row];
        [cell initWithComment:tempComment];
    
    return  cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
}


- (IBAction)backButtonClicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (IBAction)sendAction:(id)sender
{
    if ([[[LogicServices sharedInstance] getCurrentCommunity].memberType isEqualToString:@"guest"]) {
         if (_fromConversationController) {
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Guest User" message:@"As a guest, you can't comment on posts" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alert show];
         }
         else {
             UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Guest User" message:@"As a guest, you can't comment on questions" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
             [alert show];
         }
    }
    else {
    [_textField resignFirstResponder];
    if (_fromConversationController) {
        CommentModel* comment = [[CommentModel alloc]init];
        comment.content = _textField.text;
        userModel* user = [DataManagement sharedInstance].user;
        comment.imageUrl = user.imageUrl;
        comment.name = user.name;
        comment.date = [[NSDate date]timeIntervalSince1970]*1000;
        [[Networking sharedInstance] postNewCommunityPostComment:comment ForPostID:_post.postID];
        _textField.text = @"";
    }
    else {
        CommentModel* answer = [[CommentModel alloc]init];
        answer.content = _textField.text;
        userModel* user = [DataManagement sharedInstance].user;
        answer.imageUrl = user.imageUrl;
        answer.name = user.name;
        answer.date = [[NSDate date]timeIntervalSince1970]*1000;
        [[Networking sharedInstance] postNewQuestionComment:answer forPostID:_post.postID];
        _textField.text = @"";
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadData"
                                                        object:nil
                                                      userInfo:nil];
    }
}

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    [UIView animateWithDuration:0.2 animations:^{
        _bottomView.frame = CGRectMake(0, (_bottomView.frame.origin.y - 215), _bottomView.frame.size.width, _bottomView.frame.size.height);
    }
                     completion:^(BOOL finished) {
                         
                     }];
    
    
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    [UIView animateWithDuration:0.2 animations:^{
        _bottomView.frame = CGRectMake(0, (_bottomView.frame.origin.y + 215), _bottomView.frame.size.width, _bottomView.frame.size.height);
    }
                     completion:^(BOOL finished) {
                         
                     }];
}

@end
