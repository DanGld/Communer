//
//  SignInViewController.m
//  Smart Community
//
//  Created by oren shany on 7/23/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "SignInViewController.h"
#import "Networking.h"
#import "AppDelegate.h"
#import "VarificationViewController.h"


@interface SignInViewController ()
{
    BOOL isSentSMS;
    NSTimer *inputCodeTimer;
}
@end

@implementation SignInViewController
NSString* responseData;

- (void)viewDidLoad {
    [super viewDidLoad];
    _phoneTextfield.delegate = self;
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(getDataCallback)
                                                 name:@"getFirstTimeData"
                                               object:responseData];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(getWrongCodeError)
                                                 name:@"errorVarCode"
                                               object:responseData];

}

-(void)viewWillAppear:(BOOL)animated {
    isSentSMS = NO;
    AppDelegate* delegate = [UIApplication sharedApplication].delegate;
    NSString *strCountryName = delegate.countryName;
    if(strCountryName.length > 5){
        strCountryName = [strCountryName substringToIndex:5];
    }
    if([delegate.countryName isEqualToString:@""] || [delegate.countryCode isEqualToString:@""]){
        [self.signupButton setEnabled:NO];
    }else{
        [self.codeLabel setTitle:[NSString stringWithFormat:@"(%@ %@)", strCountryName, delegate.countryCode] forState:UIControlStateNormal];
        [self.codeLabel setTitle:[NSString stringWithFormat:@"(%@ %@)", strCountryName, delegate.countryCode] forState:UIControlStateHighlighted];
        [self.signupButton setEnabled:YES];
    }
    [self.codeLabel.titleLabel setFont:[UIFont fontWithName:@"Lato-Regular" size:self.codeLabel.titleLabel.font.pointSize]];
    [self.toSignLabel setFont:[UIFont fontWithName:@"Lato-Regular" size:self.toSignLabel.font.pointSize]];
    [self.verifyView setHidden:YES];
    [self.verifyDetailLabel setFont:[UIFont fontWithName:@"Lato-Regular" size:self.verifyDetailLabel.font.pointSize]];
    [self.codeView setHidden:YES];
    [self.loader setHidden:YES];
}


- (IBAction)onSignUpAction
{
    [self.view endEditing:YES];
    if (![_codeLabel.titleLabel.text isEqualToString:@""] && ![_phoneTextfield.text isEqualToString:@""] ) {
        if(!isSentSMS) {
            [self.verifyView setHidden:NO];
            [self.signupButton setEnabled:NO];
            NSString* phoneNum = [NSString stringWithFormat:@"%@%@", _codeLabel.titleLabel.text, _phoneTextfield.text];
            phoneNum = [phoneNum stringByReplacingOccurrencesOfString:@" " withString:@""];
            Networking* network = [[Networking alloc]init];
            [network getSMSCodeWithPhoneNumber:phoneNum];
            // Auto calculate radius
            [self.verifyTimerBgView drawCircleWithPercent:100
                                                 duration:2
                                                lineWidth:8
                                                clockwise:YES
                                                  lineCap:kCALineCapRound
                                                fillColor:[UIColor clearColor]
                                              strokeColor:[UIColor blackColor]
                                           animatedColors:nil];
            
            self.verifyTimerBgView.percentLabel.font = [UIFont systemFontOfSize:0];
            [self.verifyTimerBgView startAnimation];
            
            [self.verifyTimerView drawCircleWithPercent:100
                                               duration:60
                                              lineWidth:8
                                              clockwise:YES
                                                lineCap:kCALineCapRound
                                              fillColor:[UIColor clearColor]
                                            strokeColor:[UIColor whiteColor]
                                         animatedColors:nil];
            self.verifyTimerView.percentLabel.font = [UIFont fontWithName:@"Lato-Bold" size:24];
            [self.verifyTimerView startAnimation];
            // Check 15second
            inputCodeTimer = [NSTimer scheduledTimerWithTimeInterval:15
                                                              target:self
                                                            selector:@selector(inputVerifyCode:) // <== see the ':', indicates your function takes an argument
                                                            userInfo:nil 
                                                             repeats:NO];
        }else{
            [self.loader setHidden:NO];
            [self.loader startAnimating];
            [[Networking sharedInstance] getHushTokenWithVarCode:self.varcodeTextfield.text];
        }

//        VarificationViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"VarificationViewController"];
//        [self.navigationController pushViewController:controller animated:YES];
    }
    else {
        isSentSMS = NO;
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Missing phone number" message:@"Please fill your phone number for signing in to Communer" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK" , nil];
        [alert show];
    }
    
}

-(void)getDataCallback {
    
    [self.loader stopAnimating];
    [self.loader setHidden:YES];
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"Success Verification Code!" message:@"The verification code youv'e entered is correct." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
}

-(void)getWrongCodeError {
    [self.loader stopAnimating];
    [self.loader setHidden:YES];
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Wrong Verification Code" message:@"The verification code youv'e entered is wrong, please try again." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
}

- (void)inputVerifyCode:(NSTimer *)timer {
    isSentSMS = YES;
    if(inputCodeTimer){
        [inputCodeTimer invalidate];
        inputCodeTimer = nil;
    }
    [self.codeView setHidden:NO];
    
}

-(void)textFieldDidBeginEditing:(UITextField *)textField {
    if ([[UIScreen mainScreen]bounds].size.height == 480) { // iPhone 4
        [UIView animateWithDuration:0.3 animations:^{
            if(textField.tag == 0) {
                [_toSignLabel setCenter:CGPointMake(_toSignLabel.center.x, _toSignLabel.center.y-580)];
                [_signView setCenter:CGPointMake(_signView.center.x, _signView.center.y-150)];
            }else{
                [self.codeView setCenter:CGPointMake(self.codeView.center.x, self.codeView.center.y - 250)];
            }
        }];
    }
    else {
        [UIView animateWithDuration:0.3 animations:^{
            if(textField.tag == 0) {
                [_toSignLabel setCenter:CGPointMake(_toSignLabel.center.x, _toSignLabel.center.y-500)];
                [_signView setCenter:CGPointMake(_signView.center.x, _signView.center.y-70)];
            }else{
                [self.codeView setCenter:CGPointMake(self.codeView.center.x, self.codeView.center.y - 170)];
            }
        }];
    }
}

-(void)textFieldDidEndEditing:(UITextField *)textField {
    if ([[UIScreen mainScreen]bounds].size.height == 480) { // iPhone 4
        [UIView animateWithDuration:0.3 animations:^{
            if(textField.tag == 0) {
                [_toSignLabel setCenter:CGPointMake(_toSignLabel.center.x, _toSignLabel.center.y+580)];
                [_signView setCenter:CGPointMake(_signView.center.x, _signView.center.y+150)];
            }else{
                [self.codeView setCenter:CGPointMake(self.codeView.center.x, self.codeView.center.y + 250)];
            }
        }];
    }
    else {
        [UIView animateWithDuration:0.3 animations:^{
            if(textField.tag == 0) {
                [_toSignLabel setCenter:CGPointMake(_toSignLabel.center.x, _toSignLabel.center.y+500)];
                [_signView setCenter:CGPointMake(_signView.center.x, _signView.center.y+70)];
            }else{
                [self.codeView setCenter:CGPointMake(self.codeView.center.x, self.codeView.center.y + 170)];
            }
        }];
    }
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {   // return NO to not change text
    if(textField.tag == 1) {
        if(self.varcodeTextfield.text.length >= 3) {
            [self.signupButton setEnabled:YES];
        }else{
            [self.signupButton setEnabled:NO];
        }
    }
    return YES;
}
-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    [self.view endEditing:YES];
}

@end
