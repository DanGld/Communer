//
//  CreateReportViewController.h
//  Smart Community
//
//  Created by Eyal Makover on 9/28/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MVPlaceSearchTextField.h"

@interface CreateReportViewController : UIViewController <UITextFieldDelegate, PlaceSearchTextFieldDelegate>

- (IBAction)backClicked;
@property (strong, nonatomic) IBOutlet UIScrollView *scrollView;
@property (strong, nonatomic) IBOutlet MVPlaceSearchTextField *locationTextfield;
- (IBAction)openGeocoder;
@property (strong, nonatomic) IBOutlet UITextView *descriptionTextView;
@property (strong, nonatomic) IBOutlet UILabel *titleLabel;
@property (strong, nonatomic) IBOutlet UILabel *locationLabel;
@property (strong, nonatomic) IBOutlet UILabel *typeOfReportLabel;
@property (strong, nonatomic) IBOutlet UILabel *antiSemitismLabel;
@property (strong, nonatomic) IBOutlet UILabel *suspiciousLabel;
@property (strong, nonatomic) IBOutlet UILabel *aboutEvent;
@property (strong, nonatomic) IBOutlet UILabel *attackLabel;
@property (strong, nonatomic) IBOutlet UILabel *descLabel;
@property (strong, nonatomic) IBOutlet UILabel *policeHasBeenReportedLabel;
@property (strong, nonatomic) IBOutlet UILabel *remainAnonimusLabel;
@property (strong, nonatomic) IBOutlet UILabel *severityOfEventLabel;
@property (strong, nonatomic) IBOutlet UILabel *highSeverityLabel;
@property (strong, nonatomic) IBOutlet UILabel *moderateSeverityLabel;
@property (strong, nonatomic) IBOutlet UILabel *lowSeverityLabel;
@property (strong, nonatomic) IBOutlet UIImageView *reportCheckbox1;
@property (strong, nonatomic) IBOutlet UIImageView *reportCheckbox2;
@property (strong, nonatomic) IBOutlet UIImageView *reportCheckbox3;
@property (strong, nonatomic) IBOutlet UIImageView *reportCheckbox4;
@property (strong, nonatomic) IBOutlet UIImageView *policeCheckbox;
@property (strong, nonatomic) IBOutlet UIImageView *anonymousCheckbox;
@property (strong, nonatomic) IBOutlet UIImageView *highRadio;
@property (strong, nonatomic) IBOutlet UIImageView *moderateRadio;
@property (strong, nonatomic) IBOutlet UIImageView *lowRadio;
- (IBAction)typeOfReportClicked:(UIButton *)sender;
- (IBAction)checkButtonsClicked:(UIButton *)sender;
- (IBAction)severityClicked:(UIButton *)sender;

@end
