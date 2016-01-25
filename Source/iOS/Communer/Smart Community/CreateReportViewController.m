//
//  CreateReportViewController.m
//  Smart Community
//
//  Created by Eyal Makover on 9/28/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "CreateReportViewController.h"
#import "AppDelegate.h"
#import "ReportModel.h"
#import "Networking.h"

@interface CreateReportViewController ()

@end

@implementation CreateReportViewController

NSString* typeOfReport;
NSString* reportSeverity;
BOOL isPoliceReported;
BOOL isAnonimous;
NSString* reportCoords;
NSString* reportLocationTitle;

- (void)viewDidLoad {
    [super viewDidLoad];
    [_scrollView setContentSize:CGSizeMake(_scrollView.frame.size.width, 667)];
    _locationTextfield.placeSearchDelegate = self;
    _locationTextfield.strApiKey = @"AIzaSyDMVM_Ge2EV4a1W6-bKgl4q9tNQvcan6vU";
    AppDelegate* delegate = (AppDelegate*)[UIApplication sharedApplication].delegate;
    [_locationTextfield setText:delegate.currentLocation.title];
    reportLocationTitle = delegate.currentLocation.title;
    reportCoords = delegate.currentLocation.coords;
    typeOfReport = @"antiSemitism";
    reportSeverity = @"high";
    isPoliceReported = NO;
    isAnonimous = NO;
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [_descriptionTextView.layer setBorderWidth:2.0f];
    
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"hebrew"]) {
        [_titleLabel setText:@"דיווח חדש"];
        [_locationLabel setText:@"מיקום"];
        [_typeOfReportLabel setText:@"סוג הדיווח"];
        [_antiSemitismLabel setText:@"רקע אנטישמי"];
        [_suspiciousLabel setText:@"חפץ חשוד"];
        [_aboutEvent setText:@"אירוע או הפגנה"];
        [_attackLabel setText:@"תקיפה"];
        [_descLabel setText:@"תיאור"];
        [_policeHasBeenReportedLabel setText:@"האם דווח למשטרה?"];
        [_remainAnonimusLabel setText:@"שלח דיווח אנונימי"];
        [_severityOfEventLabel setText:@"חומרת הדיווח"];
        [_highSeverityLabel setText:@"חמור" ];
        [_moderateSeverityLabel setText:@"בינוני"];
        [_lowSeverityLabel setText:@"קל"];
    }
}



- (IBAction)backClicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (IBAction)openGeocoder {
    
}

#pragma mark - Place search Textfield Delegates

-(void)placeSearch:(MVPlaceSearchTextField*)textField ResponseForSelectedPlace:(GMSPlace*)responseDict{
    [self.view endEditing:YES];
    reportCoords = [NSString stringWithFormat:@"%f,%f", [responseDict coordinate].latitude, [responseDict coordinate].longitude];
    reportLocationTitle = responseDict.name;
}
-(void)placeSearchWillShowResult:(MVPlaceSearchTextField*)textField{
    
}
-(void)placeSearchWillHideResult:(MVPlaceSearchTextField*)textField{
    
}
-(void)placeSearch:(MVPlaceSearchTextField*)textField ResultCell:(UITableViewCell*)cell withPlaceObject:(PlaceObject*)placeObject atIndex:(NSInteger)index{
    if(index%2==0){
        cell.contentView.backgroundColor = [UIColor colorWithWhite:0.9 alpha:1.0];
    }else{
        cell.contentView.backgroundColor = [UIColor whiteColor];
    }
}


- (IBAction)typeOfReportClicked:(UIButton *)sender {
    [self.view endEditing:YES];
    
    if ([sender.restorationIdentifier isEqualToString:@"antiSemitism"]) {
        [_reportCheckbox1 setImage:[UIImage imageNamed:@"radio_button_on"]];
        [_reportCheckbox2 setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_reportCheckbox3 setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_reportCheckbox4 setImage:[UIImage imageNamed:@"radio_button_off"]];
        
        typeOfReport = @"antiSemitism";
    }
    if ([sender.restorationIdentifier isEqualToString:@"suspiciousObject"]) {
        [_reportCheckbox1 setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_reportCheckbox2 setImage:[UIImage imageNamed:@"radio_button_on"]];
        [_reportCheckbox3 setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_reportCheckbox4 setImage:[UIImage imageNamed:@"radio_button_off"]];
        typeOfReport = @"suspiciousObject";
    }
    if ([sender.restorationIdentifier isEqualToString:@"reportAboutEvent"]) {
        [_reportCheckbox1 setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_reportCheckbox2 setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_reportCheckbox3 setImage:[UIImage imageNamed:@"radio_button_on"]];
        [_reportCheckbox4 setImage:[UIImage imageNamed:@"radio_button_off"]];
        typeOfReport = @"reportAboutEvent";
    }
    if ([sender.restorationIdentifier isEqualToString:@"attack"]) {
        [_reportCheckbox1 setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_reportCheckbox2 setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_reportCheckbox3 setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_reportCheckbox4 setImage:[UIImage imageNamed:@"radio_button_on"]];
    }
    typeOfReport = @"attack";
}

- (IBAction)checkButtonsClicked:(UIButton *)sender {
    [self.view endEditing:YES];

    if ([sender.restorationIdentifier isEqualToString:@"policeReported"]) {
        if (!isPoliceReported)
        [_policeCheckbox setImage:[UIImage imageNamed:@"check_box_full_green"]];
        else
            [_policeCheckbox setImage:[UIImage imageNamed:@"check_box_empty"]];
        
        isPoliceReported = !isPoliceReported;
        
    }
    if ([sender.restorationIdentifier isEqualToString:@"remainAnonymously"]) {
        if (!isAnonimous)
        [_anonymousCheckbox setImage:[UIImage imageNamed:@"check_box_full_green"]];
        else
        [_anonymousCheckbox setImage:[UIImage imageNamed:@"check_box_empty"]];
    }
    
    isAnonimous = !isAnonimous;
}

- (IBAction)severityClicked:(UIButton *)sender {
    [self.view endEditing:YES];

    if ([sender.restorationIdentifier isEqualToString:@"high"]) {
        [_highRadio setImage:[UIImage imageNamed:@"radio_button_on"]];
        [_moderateRadio setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_lowRadio setImage:[UIImage imageNamed:@"radio_button_off"]];
        reportSeverity = @"high";
    }
    if ([sender.restorationIdentifier isEqualToString:@"moderate"]) {
        [_highRadio setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_moderateRadio setImage:[UIImage imageNamed:@"radio_button_on"]];
        [_lowRadio setImage:[UIImage imageNamed:@"radio_button_off"]];
        reportSeverity = @"moderate";
    }
    if ([sender.restorationIdentifier isEqualToString:@"low"]) {
        [_highRadio setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_moderateRadio setImage:[UIImage imageNamed:@"radio_button_off"]];
        [_lowRadio setImage:[UIImage imageNamed:@"radio_button_on"]];
        reportSeverity = @"low";
    }
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    
    [self.view endEditing:YES];
}

- (IBAction)createReportClicked {
    ReportModel* newReport = [[ReportModel alloc]init];
    newReport.location = [[LocationModel alloc]init];
    newReport.location.coords = reportCoords;
    newReport.location.title = reportLocationTitle;
    newReport.type = typeOfReport;
    newReport.desc = _descriptionTextView.text;
    newReport.isPoliceReport = isPoliceReported;
    newReport.isAnnonimus = isAnonimous;
    newReport.severity = reportSeverity;
    
    [[Networking sharedInstance] sendSecurityReport:newReport];
    
    [self dismissViewControllerAnimated:YES completion:^{
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Report Sent" message:@"Thank you for your report!" delegate:self cancelButtonTitle:nil otherButtonTitles:@"CONTINUE", nil];
        [alert show];
    }];

   
}


@end
