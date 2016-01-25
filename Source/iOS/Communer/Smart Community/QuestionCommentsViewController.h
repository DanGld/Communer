//
//  QuestionCommentsViewController.h
//  Smart Community
//
//  Created by oren shany on 9/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "postModel.h"

@interface QuestionCommentsViewController : UIViewController<UITableViewDataSource,UITableViewDelegate, UITextFieldDelegate>

@property (nonatomic) BOOL hideTable;
- (IBAction)backButtonClicked;
@property (strong, nonatomic) IBOutlet UITableView *commentsTable;
@property (strong, nonatomic) IBOutlet UIView *containerView;
@property (strong, nonatomic) NSMutableArray *commentsArray;
@property (strong, nonatomic) postModel *post;
@property (nonatomic) BOOL isPublic;
@property (nonatomic) BOOL fromConversationController;
@property (weak, nonatomic) IBOutlet UITextField *textField;
@property (weak, nonatomic) IBOutlet UIButton *sendButton;
- (IBAction)sendAction:(id)sender;
@property (weak, nonatomic) IBOutlet UIView *bottomView;
@property (weak, nonatomic) IBOutlet UILabel *questionTitle;
@property (weak, nonatomic) IBOutlet UIImageView *questionImage;
@property (weak, nonatomic) IBOutlet UITextView *questionContent;
@property (weak, nonatomic) IBOutlet UILabel *questionDate;

@end
