//
//  myProfileViewController.m
//  Smart Community
//
//  Created by oren shany on 7/27/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "myProfileViewController.h"
#import "MFSideMenu.h"
#import "UITabbarControllerViewController.h"
#import "SideMenuViewController.h"
#import "HomeViewController.h"
#import "UIImageView+AFNetworking.h"
#import "Networking.h"

@interface myProfileViewController ()

@end

@implementation myProfileViewController
CGPoint svos;
NSArray* genderArr;
NSArray* maritalArr;
NSArray* months;
NSArray* days;
NSMutableArray* years;
NSString* appLanguage;

NSArray* pickerData;
UITextField* currentTextfield;
UIImagePickerController * imagePicker;

- (void)viewDidLoad {
    [super viewDidLoad];
    self.scrollView.contentSize =CGSizeMake(self.scrollView.frame.size.width, 750);
    userModel* user = [DataManagement sharedInstance].user;
    _firstNameTextField.delegate = self;
    _lastNameTextField.delegate = self;
    _genderTextField.delegate = self;
    _maritalTextfield.delegate = self;
    _bDayDayTextfield.delegate = self;
    _bDayMonthTextfield.delegate = self;
    _bDayYearTextfield.delegate = self;
    _emailTextField.delegate = self;
    _phoneTextField.delegate = self;
    
    
    imagePicker = [[UIImagePickerController alloc] init];
    imagePicker.delegate = self;
    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera])
    {
    imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
    }
    
    appLanguage = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    
    NSString* data;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(UpdateUserDetailsSuccess)
                                                 name:@"UpdateUserDetailsSuccess"
                                               object:data];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(UpdateUserDetailsFail)
                                                 name:@"UpdateUserDetailsFail"
                                               object:data];
    
    _firstNameTextField.text = user.name;
    _lastNameTextField.text = user.lastname;
    _genderTextField.text = user.gender;
    _maritalTextfield.text = user.mStatus;
    _bDayMonthTextfield.text = user.userBMonth;
    _bDayDayTextfield.text = user.userBDay;
    _bDayYearTextfield.text = user.userBYear;
    _emailTextField.text = user.email;
    _phoneTextField.text = [[NSUserDefaults standardUserDefaults] objectForKey:@"phoneNumber"];
    
    [_userImage setImageWithURL:[NSURL URLWithString:user.imageUrl] placeholderImage:[UIImage imageNamed:@"communer_placeholder"]];
    
    genderArr = @[@"Male", @"Female"];
    maritalArr = @[@"Single", @"Married", @"Prefer not to say"];
    months = @[@"January", @"Fabruary", @"March", @"April",@"May",@"June",@"July",@"August",@"September",@"October",@"November",@"December"];
    days = @[@"01",@"02",@"03",@"04",@"05",@"06",@"07",@"08",@"09",@"10",@"11",@"12",@"13",@"14",@"15",@"16",@"17",@"18",@"19",@"20",@"21",@"22",@"23",@"24",@"25",@"26",@"27",@"28",@"29",@"30",@"31",];
    years = [NSMutableArray array];
    for (int i=1930 ; i<2015 ; i++) {
        [years addObject:[NSString stringWithFormat:@"%i", i]];
    }
    
    _pickerView.delegate = self;
    _pickerView.dataSource = self;
    
    if (!_isUpdateDetails) {
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Your number has successfully been identified" message:@"Please add your details and then you can begin connecting to your communities." delegate:nil cancelButtonTitle:nil otherButtonTitles:@"CONTINUE", nil];
        [alert show];
    }
}

- (void)imagePickerController:(UIImagePickerController *)picker
didFinishPickingMediaWithInfo:(NSDictionary *)info {
    
    [picker dismissViewControllerAnimated:YES completion:nil];
    _userImage.image = [info objectForKey:UIImagePickerControllerOriginalImage];
    [DataManagement sharedInstance].user.userImage = _userImage.image;
    
}

-(void)UpdateUserDetailsSuccess {
    [_activityLoader stopAnimating];
    [[NSUserDefaults standardUserDefaults] setObject:appLanguage forKey:@"appLanguage"];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"appLanguageChange"
                                                        object:nil
                                                      userInfo:nil];
    
    if (_isUpdateDetails) {
        [self dismissViewControllerAnimated:YES completion:nil];
    }
    else {
        UITabbarControllerViewController *tabbar = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"main.tabbar"];
        
        SideMenuViewController *sideMenu = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"main.SideMenu"];
        
        MFSideMenuContainerViewController *container = [MFSideMenuContainerViewController
                                                        containerWithCenterViewController:tabbar
                                                        leftMenuViewController:sideMenu
                                                        rightMenuViewController:nil];
        [self presentViewController:container animated:YES completion:nil];
    }
    
}

-(void)UpdateUserDetailsFail {
    [_activityLoader stopAnimating];
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Server Error" message:@"Failed to update your profile details, try again later" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
    }
    if ([language isEqualToString:@"hebrew"]) {
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"תקלת סרבר" message:@"עדכון הנתונים נכשל, נסה שוב מאוחר יותר." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alert show];
    }
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];

    if (_isUpdateDetails) {
        if ([language isEqualToString:@"english"]) {
            [self englishButtonClicked];
            [_titleLabel setText:@"My Profile"];
        }
        if ([language isEqualToString:@"hebrew"]) {
            [self hebrewButtonClicked];
            [_titleLabel setText:@"הפרופיל שלי"];
            [_firstNameTitle setText:@"שם פרטי"];
            [_lastNameTitle setText:@"שם משפחה"];
            [_genderTitle setText:@"מגדר"];
            [_maritalTitle setText:@"מצב זוגי"];
            [_birthdayTitle setText:@"תאריך לידה"];
            [_emailTitle setText:@"דוא״ל"];
            [_phonenumTitle setText:@"טלפון"];
            [_languageTitle setText:@"שפה"];
            [_saveButton setTitle:@"שמור" forState: UIControlStateNormal];
            [_tellUsTitle setText:@"ספר/י לנו קצת עליך, כדי שנשלח לך רק מה שמעניין אותך"];
         }

        [_signUpButton setHidden:YES];
        [_saveButton setHidden:NO];
    }
    else
    {
        [_backButton setHidden:YES];
    }
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    svos = _scrollView.contentOffset;
    CGPoint pt;
    CGRect rc = [textField bounds];
    rc = [textField convertRect:rc toView:_scrollView];
    pt = rc.origin;
    pt.x = 0;
    pt.y -= 60;
    [_scrollView setContentOffset:pt animated:YES];
    
    UIView* dummyView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
    
    if ([textField.restorationIdentifier isEqualToString:@"firstName"]) {
        [_pickerView setHidden:YES];
    }
    if ([textField.restorationIdentifier isEqualToString:@"lastName"]) {
        [_pickerView setHidden:YES];
    }
    if ([textField.restorationIdentifier isEqualToString:@"gender"]) {
        pickerData = genderArr;
        [_pickerView setHidden:NO];
        
        textField.inputView = dummyView;
    }
    if ([textField.restorationIdentifier isEqualToString:@"marital"]) {
        pickerData = maritalArr;
        [_pickerView setHidden:NO];
         textField.inputView = dummyView;
    }
    if ([textField.restorationIdentifier isEqualToString:@"month"]) {
        pickerData = months;
        [_pickerView setHidden:NO];
         textField.inputView = dummyView;
    }
    if ([textField.restorationIdentifier isEqualToString:@"day"]) {
        pickerData = days;
        [_pickerView setHidden:NO];
         textField.inputView = dummyView;
    }
    if ([textField.restorationIdentifier isEqualToString:@"year"]) {
        pickerData = years;
        [_pickerView setHidden:NO];
         textField.inputView = dummyView;
    }
    currentTextfield = textField;
    [_pickerView reloadAllComponents];
    
    if ([textField.restorationIdentifier isEqualToString:@"gender"] || [textField.restorationIdentifier isEqualToString:@"marital"] || [textField.restorationIdentifier isEqualToString:@"month"] || [textField.restorationIdentifier isEqualToString:@"day"] || [textField.restorationIdentifier isEqualToString:@"year"]) {
        if ([textField.text isEqualToString:@""]) {
            [_pickerView selectRow:0 inComponent:0 animated:NO];
        }
        else {
            for (int i=0; i<pickerData.count; i++) {
                if ([pickerData[i] isEqualToString:textField.text]) {
                    [_pickerView selectRow:i inComponent:0 animated:NO];
                }
            }
        }
    }
    
    [_firstNameTextField setText:[_firstNameTextField.text capitalizedString]];
    [_lastNameTextField setText:[_lastNameTextField.text capitalizedString]];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    
    if ([textField.restorationIdentifier isEqualToString:@"firstName"]) {
        [_lastNameTextField becomeFirstResponder];
    }
    if ([textField.restorationIdentifier isEqualToString:@"lastName"]) {
        [_genderTextField becomeFirstResponder];
    }
    if ([textField.restorationIdentifier isEqualToString:@"email"]) {
        [_scrollView setContentOffset:svos animated:YES];
        [textField resignFirstResponder];
    }
    [_pickerView setHidden:YES];
    return YES;
}

- (IBAction)signUpAction:(id)sender
{
    [self updateUserDetailsClicked];
}

-(NSString*)getMonthNumFromString:(NSString*)monthString {
    if ([monthString isEqualToString:months[0]]) {
        return @"01";
    }
    if ([monthString isEqualToString:months[1]]) {
        return @"02";
    }
    if ([monthString isEqualToString:months[2]]) {
        return @"03";
    }
    if ([monthString isEqualToString:months[3]]) {
        return @"04";
    }
    if ([monthString isEqualToString:months[4]]) {
        return @"05";
    }
    if ([monthString isEqualToString:months[5]]) {
        return @"06";
    }
    if ([monthString isEqualToString:months[6]]) {
        return @"07";
    }
    if ([monthString isEqualToString:months[7]]) {
        return @"08";
    }
    if ([monthString isEqualToString:months[8]]) {
        return @"09";
    }
    if ([monthString isEqualToString:months[9]]) {
        return @"10";
    }
    if ([monthString isEqualToString:months[10]]) {
        return @"11";
    }
    if ([monthString isEqualToString:months[11]]) {
        return @"12";
    }
    return @"";
}

- (IBAction)updateUserDetailsClicked {
    [self.view endEditing:YES];
    [_pickerView setHidden:YES];
    [self.scrollView setContentOffset:
     CGPointMake(0, -self.scrollView.contentInset.top) animated:YES];
    [_activityLoader startAnimating];
    
    NSString *stringDate = [NSString  stringWithFormat:@"%@ %@, %@", [self getMonthNumFromString:_bDayMonthTextfield.text], _bDayDayTextfield.text, _bDayYearTextfield.text ];
    NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
    [dateFormat setDateFormat:@"MM dd, yyyy"];
    NSDate *date = [dateFormat dateFromString:stringDate];
    NSTimeInterval time = [date timeIntervalSince1970]*1000;
    
    [_firstNameTextField setText:[_firstNameTextField.text capitalizedString]];
    [_lastNameTextField setText:[_lastNameTextField.text capitalizedString]];
    
    userModel* user = [DataManagement sharedInstance].user;
    user.name = _firstNameTextField.text;
    user.lastname = _lastNameTextField.text;
    user.gender = _genderTextField.text;
    user.mStatus = _maritalTextfield.text;
    user.bDayInt = time;
    user.userBDay = _bDayDayTextfield.text;
    user.userBMonth = _bDayMonthTextfield.text;
    user.userBYear = _bDayYearTextfield.text;
    user.email = _emailTextField.text;
    [[Networking sharedInstance] updateUserDetails:user];
    
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    
    [_firstNameTextField setText:[_firstNameTextField.text capitalizedString]];
    [_lastNameTextField setText:[_lastNameTextField.text capitalizedString]];

    [self.view endEditing:YES];
    [self.scrollView setContentOffset:
     CGPointMake(0, -self.scrollView.contentInset.top) animated:YES];
}

- (IBAction)changePictureClicked {
     NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
    UIActionSheet *popup = [[UIActionSheet alloc] initWithTitle:@"Select Image Source:" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:
                            @"Take A Photo",
                            @"Pick From Gallery",
                            nil];
    popup.tag = 1;
    [popup showInView:[UIApplication sharedApplication].keyWindow];
    }
    if ([language isEqualToString:@"hebrew"]) {
        UIActionSheet *popup = [[UIActionSheet alloc] initWithTitle:@"בחר מקור תמונה" delegate:self cancelButtonTitle:@"ביטול" destructiveButtonTitle:nil otherButtonTitles:
                                @"צלם תמונה",
                                @"בחר מגלריה",
                                nil];
        popup.tag = 1;
        [popup showInView:[UIApplication sharedApplication].keyWindow];
    }
}

- (IBAction)englishButtonClicked {
   appLanguage = @"english";
    [_englishRadioButton setImage:[UIImage imageNamed:@"radio_button_on"]];
    [_hebrewRadioButton setImage:[UIImage imageNamed:@"radio_button_off"]];
}

- (IBAction)hebrewButtonClicked {
    appLanguage = @"hebrew";
    [_englishRadioButton setImage:[UIImage imageNamed:@"radio_button_off"]];
    [_hebrewRadioButton setImage:[UIImage imageNamed:@"radio_button_on"]];
}

- (void)actionSheet:(UIActionSheet *)popup clickedButtonAtIndex:(NSInteger)buttonIndex {
    
    switch (popup.tag) {
        case 1: {
            switch (buttonIndex) {
                case 0:
                    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera])
                    {
                        imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
                        AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
                        if(authStatus == AVAuthorizationStatusAuthorized) {
                            [self presentViewController:imagePicker animated:YES completion:nil];
                        } else if(authStatus == AVAuthorizationStatusDenied){
                            NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
                            if ([language isEqualToString:@"english"]) {
                            UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Access Denied" message:@"Please enable access to the camera in your iPhone settings." delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
                            [alert show];
                            }
                            if ([language isEqualToString:@"hebrew"]) {
                                UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"שימוש במצלמה נחסם" message:@"עליך לאפשר לישום שימוש במצלמה, תוכל לעשות זאת בהגדרות המכשיר." delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
                                [alert show];
                            }
                        } else if(authStatus == AVAuthorizationStatusRestricted){
                            // restricted, normally won't happen
                        } else if(authStatus == AVAuthorizationStatusNotDetermined){
                            // not determined?!
                            [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
                                if(granted){
                                    [self presentViewController:imagePicker animated:YES completion:nil];
                                } else {
                                    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
                                    if ([language isEqualToString:@"english"]) {
                                        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Access Denied" message:@"Please enable access to the camera in your iPhone settings." delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
                                        [alert show];
                                    }
                                    if ([language isEqualToString:@"hebrew"]) {
                                        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"שימוש במצלמה נחסם" message:@"עליך לאפשר לישום שימוש במצלמה, תוכל לעשות זאת בהגדרות המכשיר." delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
                                        [alert show];
                                    }
                                }
                            }];
                        }
                    }
                    break;
                case 1:
                    imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
                    [self presentViewController:imagePicker animated:YES completion:nil];
                    break;
                default:
                    break;
            }
            break;
        }
        default:
            break;
    }
}


- (IBAction)backButtonClicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (NSInteger)numberOfComponentsInPickerView:
(UIPickerView *)pickerView
{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView
numberOfRowsInComponent:(NSInteger)component
{
    return pickerData.count;
}

- (NSString *)pickerView:(UIPickerView *)pickerView
             titleForRow:(NSInteger)row
            forComponent:(NSInteger)component
{
    return pickerData[row];
}

-(void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row
      inComponent:(NSInteger)component
{
    [currentTextfield setText:pickerData[row]];
}

@end
