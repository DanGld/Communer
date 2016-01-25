//
//  SupportViewController.h
//  Smart Community
//
//  Created by Eyal Makover on 9/28/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SupportViewController : UIViewController<UITextFieldDelegate,UIPickerViewDataSource,UIPickerViewDelegate>
@property (weak, nonatomic) IBOutlet UIPickerView *topicPicker;
@property (weak, nonatomic) IBOutlet UITextField *topicTextfield;
@property (weak, nonatomic) IBOutlet UITextField *titleTextfield;
@property (weak, nonatomic) IBOutlet UITextField *descriptionTextfield;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
- (IBAction)backclicked;
- (IBAction)goToWebsite;
- (IBAction)sendFeedbackClicked;
@property (strong, nonatomic) IBOutlet UILabel *titleLabel;
@property (strong, nonatomic) IBOutlet UILabel *websiteLabel;
@property (strong, nonatomic) IBOutlet UILabel *forMoreInfoLabel;
@property (strong, nonatomic) IBOutlet UILabel *orSendLabel;
@property (strong, nonatomic) IBOutlet UILabel *topicLabel;
@property (strong, nonatomic) IBOutlet UILabel *title2Label;
@property (strong, nonatomic) IBOutlet UILabel *descriptionLabel;

@end
