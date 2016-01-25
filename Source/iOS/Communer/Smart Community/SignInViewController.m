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

@end

@implementation SignInViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    _phoneTextfield.delegate = self;
}

-(void)viewWillAppear:(BOOL)animated {
    AppDelegate* delegate = [UIApplication sharedApplication].delegate;
    [self.codeLabel setTitle:delegate.countryCode forState:UIControlStateNormal];
    [self.codeLabel setTitle:delegate.countryCode forState:UIControlStateHighlighted];
}


- (IBAction)signInButton
{
    if (![_codeLabel.titleLabel.text isEqualToString:@""] && ![_phoneTextfield.text isEqualToString:@""] ) {
    NSString* phoneNum = [NSString stringWithFormat:@"%@%@", _codeLabel.titleLabel.text, _phoneTextfield.text];
        phoneNum = [phoneNum stringByReplacingOccurrencesOfString:@" " withString:@""];
    Networking* network = [[Networking alloc]init];
    [network getSMSCodeWithPhoneNumber:phoneNum];
        
        VarificationViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"VarificationViewController"];
        [self.navigationController pushViewController:controller animated:YES];
    }
    else {
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Missing phone number" message:@"Please fill your phone number for signing in to Communer" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK" , nil];
        [alert show];
    }
    
}

-(void)textFieldDidBeginEditing:(UITextField *)textField {
    if ([[UIScreen mainScreen]bounds].size.height == 480) { // iPhone 4
 [UIView animateWithDuration:0.3 animations:^{
     [_toSignLabel setCenter:CGPointMake(_toSignLabel.center.x, _toSignLabel.center.y-580)];
     [_signView setCenter:CGPointMake(_signView.center.x, _signView.center.y-150)];
 }];
    }
    else {
        [UIView animateWithDuration:0.3 animations:^{
            [_toSignLabel setCenter:CGPointMake(_toSignLabel.center.x, _toSignLabel.center.y-500)];
            [_signView setCenter:CGPointMake(_signView.center.x, _signView.center.y-70)];
        }];
    }
}

-(void)textFieldDidEndEditing:(UITextField *)textField {
    if ([[UIScreen mainScreen]bounds].size.height == 480) { // iPhone 4
    [UIView animateWithDuration:0.3 animations:^{
        [_toSignLabel setCenter:CGPointMake(_toSignLabel.center.x, _toSignLabel.center.y+580)];
        [_signView setCenter:CGPointMake(_signView.center.x, _signView.center.y+150)];
    }];
    }
    else {
        [UIView animateWithDuration:0.3 animations:^{
            [_toSignLabel setCenter:CGPointMake(_toSignLabel.center.x, _toSignLabel.center.y+500)];
            [_signView setCenter:CGPointMake(_signView.center.x, _signView.center.y+70)];
        }];
    }
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    [self.view endEditing:YES];
}

@end
