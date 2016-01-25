//
//  CreateQuestionViewController.h
//  Smart Community
//
//  Created by oren shany on 9/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CreateQuestionViewController : UIViewController<UITextViewDelegate>

@property (strong, nonatomic) IBOutlet UIButton *checkboxButton;
@property (weak, nonatomic) IBOutlet UILabel *checkBoxLabel;
@property (weak, nonatomic) IBOutlet UITextView *textDescription;
@property (nonatomic) BOOL fromConversations;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (strong, nonatomic) IBOutlet UILabel *questionTitleLabel;
@property (strong, nonatomic) IBOutlet UILabel *descriptionTitle;
@property (strong, nonatomic) IBOutlet UITextField *messageTitleLabel;
@property (strong, nonatomic) IBOutlet UILabel *writeAPostPlaceholder;

- (IBAction)backButtonClicked;
- (IBAction)postQuestionClicked;

@end
