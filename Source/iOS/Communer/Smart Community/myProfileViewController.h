//
//  myProfileViewController.h
//  Smart Community
//
//  Created by oren shany on 7/27/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DataManagement.h"

@interface myProfileViewController : UIViewController<UITextFieldDelegate,UIPickerViewDataSource,UIPickerViewDelegate,UIImagePickerControllerDelegate>
@property (nonatomic)  BOOL *isUpdateDetails;
@property (strong, nonatomic) IBOutlet UIButton *signUpButton;
@property (strong, nonatomic) IBOutlet UIButton *saveButton;
@property (strong, nonatomic) IBOutlet UIButton *backButton;
@property (strong, nonatomic) IBOutlet UIPickerView *pickerView;

@property (strong, nonatomic) IBOutlet UIImageView *userImage;
@property (strong, nonatomic) IBOutlet UILabel *titleLabel;
@property (strong, nonatomic) IBOutlet UIScrollView *scrollView;
@property (strong, nonatomic) IBOutlet UITextField *firstNameTextField;
@property (strong, nonatomic) IBOutlet UITextField *lastNameTextField;
@property (strong, nonatomic) IBOutlet UITextField *genderTextField;
@property (strong, nonatomic) IBOutlet UITextField *bDayMonthTextfield;
@property (strong, nonatomic) IBOutlet UITextField *bDayDayTextfield;
@property (strong, nonatomic) IBOutlet UITextField *bDayYearTextfield;
@property (strong, nonatomic) IBOutlet UITextField *maritalTextfield;
@property (strong, nonatomic) IBOutlet UITextField *emailTextField;
@property (strong, nonatomic) IBOutlet UITextField *phoneTextField;
@property (strong, nonatomic) IBOutlet UIActivityIndicatorView *activityLoader;
@property (strong, nonatomic) IBOutlet UIImageView *englishRadioButton;
@property (strong, nonatomic) IBOutlet UIImageView *hebrewRadioButton;
@property (strong, nonatomic) IBOutlet UILabel *firstNameTitle;
@property (strong, nonatomic) IBOutlet UILabel *lastNameTitle;
@property (strong, nonatomic) IBOutlet UILabel *genderTitle;
@property (strong, nonatomic) IBOutlet UILabel *maritalTitle;
@property (strong, nonatomic) IBOutlet UILabel *birthdayTitle;
@property (strong, nonatomic) IBOutlet UILabel *emailTitle;
@property (strong, nonatomic) IBOutlet UILabel *phonenumTitle;
@property (strong, nonatomic) IBOutlet UILabel *languageTitle;
@property (strong, nonatomic) IBOutlet UILabel *tellUsTitle;
- (IBAction)signUpAction:(id)sender;
- (IBAction)updateUserDetailsClicked;
- (IBAction)changePictureClicked;
- (IBAction)englishButtonClicked;
- (IBAction)hebrewButtonClicked;

@end
