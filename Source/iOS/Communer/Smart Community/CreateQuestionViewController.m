//
//  CreateQuestionViewController.m
//  Smart Community
//
//  Created by oren shany on 9/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "CreateQuestionViewController.h"
#import "Networking.h"
#import "QuestionModel.h"
#import "DataManagement.h"

@interface CreateQuestionViewController ()

@end

@implementation CreateQuestionViewController

BOOL isPublic ;
- (void)viewDidLoad {
    [super viewDidLoad];
    isPublic = YES;
    if (_fromConversations) {
        _titleLabel.text = @"Write A Post";
        _checkboxButton.hidden = YES;
        _checkBoxLabel.hidden = YES;
    }
    
    [_textDescription setDelegate:self];
}

-(BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
   NSString* newText = [textView.text stringByReplacingCharactersInRange:range withString:text];
    if (newText.length == 0) {
        [_writeAPostPlaceholder setHidden:NO];
    }
    else {
        [_writeAPostPlaceholder setHidden:YES];
    }
    return YES;
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"hebrew"]) {
        if (!_fromConversations) {
        [_titleLabel setText:@"כתוב/י את השאלה שלך"];
        [_questionTitleLabel setText:@"כותרת"];
        [_messageTitleLabel setPlaceholder:@"כתוב/י כותרת"];
        [_writeAPostPlaceholder setText:@"כתוב תיאור.."];
        [_descriptionTitle setText:@"תיאור"];
        [_checkBoxLabel setText:@"האם תרצה/י שהשאלה תפורסם לשאר הקהילה?"];
        }
        else {
            [_titleLabel setText:@"כתוב/י פוסט"];
            [_questionTitleLabel setText:@"כותרת"];
            [_messageTitleLabel setPlaceholder:@"כתוב/י כותרת"];
            [_descriptionTitle setText:@"תיאור"];
            [_writeAPostPlaceholder setText:@"כתוב תיאור"];
        }
    }
}

- (IBAction)isPublicCheckBoxClicked {
    if (isPublic) {
        isPublic = false;
        [_checkboxButton setImage:[UIImage imageNamed:@"check_box_empty"] forState:UIControlStateNormal];
    }
    else {
        isPublic = true;
         [_checkboxButton setImage:[UIImage imageNamed:@"check_box_full_orange"] forState:UIControlStateNormal];
    }
}

- (IBAction)backButtonClicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)postQuestionClicked {
    if (_fromConversations) {
        postModel* post = [[postModel alloc]init];
        post.title = _messageTitleLabel.text;
        post.content = _textDescription.text ;
        post.cDate = [[NSDate date] timeIntervalSince1970]*1000;
        post.communityMember = [[CommunityMemberModel alloc]init];
        post.communityMember.name = [DataManagement sharedInstance].user.name;
        [[Networking sharedInstance] postNewCommunityPost:post];
        [self dismissViewControllerAnimated:YES completion:nil];
    }
    else {
    QuestionModel* question = [[QuestionModel alloc]init];
    question.title = _messageTitleLabel.text;
    question.descr = _textDescription.text;
    question.isPublic = isPublic;
        
    [[Networking sharedInstance] postNewQuestion:question];
    [self dismissViewControllerAnimated:YES completion:nil];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadData"
                                                        object:nil
                                                      userInfo:nil];
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    [self.view endEditing:YES];
}
@end
