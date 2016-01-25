//
//  SupportViewController.m
//  Smart Community
//
//  Created by Eyal Makover on 9/28/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "SupportViewController.h"
#import "Networking.h"
#import "FeedbackModel.h"

@interface SupportViewController ()

@end

@implementation SupportViewController

CGPoint svos2;
NSArray* topics;

- (void)viewDidLoad {
    [super viewDidLoad];
    [_topicTextfield setDelegate:self];
    [_titleTextfield setDelegate:self];
    [_descriptionTextfield setDelegate:self];
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(singleTapGestureCaptured:)];
    [_scrollView addGestureRecognizer:singleTap];
    
    topics = @[@"Report a bug/issue", @"Suggest improvement", @"Contact my community"];

    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"hebrew"]) {
        topics = @[@"דווח על בעיה/באג", @"הצע שיפור", @"יצירת קשר עם הקהילה שלי"];
    }
    
    [_topicPicker setDelegate:self];
    [_topicPicker setDataSource:self];
    [_topicPicker setHidden:YES];
}

- (NSInteger)numberOfComponentsInPickerView:
(UIPickerView *)pickerView
{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView
numberOfRowsInComponent:(NSInteger)component
{
    return topics.count;
}

- (NSString *)pickerView:(UIPickerView *)pickerView
             titleForRow:(NSInteger)row
            forComponent:(NSInteger)component
{
    return topics[row];
}

-(void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row
      inComponent:(NSInteger)component
{
    [_topicTextfield setText:topics[row]];
}


-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"hebrew"]) {
    [_titleLabel setText:@"תמיכה"];
    [_forMoreInfoLabel setText:@"למידע נוסף בקרו באתר שלנו"];
    [_websiteLabel setText:@"אתר"];
    [_orSendLabel setText:@"או שלחו לנו הודעה"];
    [_topicLabel setText:@"נושא"];
    [_topicTextfield setPlaceholder:@"דווח על בעיה/באג"];
    [_title2Label setText:@"כותרת"];
    [_descriptionLabel setText:@"תיאור"];
    }
}

-(BOOL)textFieldShouldBeginEditing:(UITextField *)textField {
    if ([textField.restorationIdentifier isEqualToString:@"topic"])
    {
        [_topicPicker setHidden:NO];
        return NO;
    }
    else {
        [_topicPicker setHidden:YES];
        return YES;

    }
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    svos2 = _scrollView.contentOffset;
    CGPoint pt;
    CGRect rc = [textField bounds];
    rc = [textField convertRect:rc toView:_scrollView];
    pt = rc.origin;
    pt.x = 0;
    pt.y -= 60;
    [_scrollView setContentOffset:pt animated:YES];
}

- (void)singleTapGestureCaptured:(UITapGestureRecognizer *)gesture
{
    [self.view endEditing:YES];
    [self.scrollView setContentOffset:
     CGPointMake(0, -self.scrollView.contentInset.top) animated:YES];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [_scrollView setContentOffset:svos2 animated:YES];
    [textField resignFirstResponder];
    [self.scrollView setContentOffset:
     CGPointMake(0, -self.scrollView.contentInset.top) animated:YES];
    return YES;
}



-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    [self.view endEditing:YES];
    [self.scrollView setContentOffset:
     CGPointMake(0, -self.scrollView.contentInset.top) animated:YES];
    [_topicPicker setHidden:YES];
}

- (IBAction)backclicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)goToWebsite {
[[UIApplication sharedApplication] openURL:[NSURL URLWithString:@"http://www.communer.co"]];
}

- (IBAction)sendFeedbackClicked {
    FeedbackModel* feedback = [[FeedbackModel alloc]init];
    feedback.topic = _topicTextfield.text;
    feedback.title = _titleTextfield.text;
    feedback.descr = _descriptionTextfield.text;
    
    [[Networking sharedInstance] sendFeedback:feedback];
    [self dismissViewControllerAnimated:YES completion:^{
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Thank you!" message:@"Your feedback is very important for us" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"CONTINUE", nil];
        [alert show];
    }];
}
@end
