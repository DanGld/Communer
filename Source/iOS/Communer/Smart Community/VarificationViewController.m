//
//  VarificationViewController.m
//  Smart Community
//
//  Created by oren shany on 7/23/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "VarificationViewController.h"
#import <FBSDKLoginKit/FBSDKLoginKit.h>
#import <FBSDKCoreKit/FBSDKGraphRequest.h>
#import "Networking.h"
#import "DataManagement.h"
#import "myProfileViewController.h"


@interface VarificationViewController ()

@end

@implementation VarificationViewController
NSString* data;

- (void)viewDidLoad {
    [super viewDidLoad];
    _varCodeTextfield.delegate = self;
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(getDataCallback)
                                                 name:@"getFirstTimeData"
                                               object:data];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(getWrongCodeError)
                                                 name:@"errorVarCode"
                                               object:data];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(getGoogleUserDetails)
                                                 name:@"googleUserDetails"
                                               object:data];


     [GIDSignIn sharedInstance].uiDelegate = self;
}

- (void)signInWillDispatch:(GIDSignIn *)signIn error:(NSError *)error {
    
}

// Present a view that prompts the user to sign in with Google
- (void)signIn:(GIDSignIn *)signIn
presentViewController:(UIViewController *)viewController {
    [self presentViewController:viewController animated:YES completion:nil];
}

// Dismiss the "Sign in with Google" view
- (void)signIn:(GIDSignIn *)signIn
dismissViewController:(UIViewController *)viewController {
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(void)getGoogleUserDetails {
    myProfileViewController* vc =[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"updateDetails"];
    [self.navigationController pushViewController:vc animated:YES];
}


- (IBAction)backButtonClicked {
    [self.navigationController popViewControllerAnimated:YES];
}

- (IBAction)signInButton {
    [self.loader setHidden:NO];
    [self.loader startAnimating];
    [[Networking sharedInstance] getHushTokenWithVarCode:_varCodeTextfield.text];
}

-(void)getDataCallback {
    
    [self.loader stopAnimating];
    [self.loader setHidden:YES];
    [self.loginPopupView setHidden:NO];
    [self.popupBlackView setHidden:NO];
    [self.view endEditing:YES];
        //UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"Server Error" message:@"Ohh.. something went wrong, try again" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        //[alert show];
}

-(void)getWrongCodeError {
    [self.loader stopAnimating];
    [self.loader setHidden:YES];
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Wrong Verification Code" message:@"The verification code youv'e entered is wrong, please try again." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
}

- (IBAction)facebookSignUp {
    
    FBSDKLoginManager *login = [[FBSDKLoginManager alloc] init];
    [login logInWithReadPermissions:@[@"email",@"user_birthday"] handler:^(FBSDKLoginManagerLoginResult *result, NSError *error) {
        if (error) {
            // Process error
        } else if (result.isCancelled) {
            // Handle cancellations
        } else {
            // If you ask for multiple permissions at once, you
            // should check if specific permissions missing
            if ([result.grantedPermissions containsObject:@"email"]) {
                NSDictionary *params = @{@"fields":@"id,email,first_name,last_name,gender,birthday"};
                    [[[FBSDKGraphRequest alloc] initWithGraphPath:@"me" parameters:params]
                     startWithCompletionHandler:^(FBSDKGraphRequestConnection *connection, id result, NSError *error) {
                         if (!error) {
                             NSLog(@"fetched user:%@", result);
                             userModel* user = [DataManagement sharedInstance].user;
                             NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
                             [formatter setDateFormat:@"MM/dd/yyyy"];
                             user.bDayInt = [[formatter dateFromString:[result objectForKey:@"birthday"]] timeIntervalSince1970]*1000+(60*60*24);
                             
                             if (user.bDayInt!=0) {
                                 NSDate* userBDate = [NSDate dateWithTimeIntervalSince1970:user.bDayInt/1000];
                                 NSDateFormatter *df = [[NSDateFormatter alloc] init];
                                 
                                 [df setDateFormat:@"dd"];
                                 user.userBDay = [df stringFromDate:userBDate];
                                 
                                 [df setDateFormat:@"MMM"];
                                 user.userBMonth = [df stringFromDate:userBDate];
                                 
                                 [df setDateFormat:@"yy"];
                                 user.userBYear = [df stringFromDate:userBDate];
                             }
                    
                             user.name = [result objectForKey:@"first_name"];
                             user.lastname = [result objectForKey:@"last_name"];
                             user.gender = [result objectForKey:@"gender"];
                             user.email = [result objectForKey:@"email"];
                             user.imageUrl = [NSString stringWithFormat:@"http://graph.facebook.com/%@/picture?type=large", [result objectForKey:@"id"]];
                             myProfileViewController* vc =[[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"updateDetails"];
                             [self.navigationController pushViewController:vc animated:YES];
                         }
                     }];
                
            }
        }
    }];
}

-(void)textFieldDidBeginEditing:(UITextField *)textField {
    if ([[UIScreen mainScreen]bounds].size.height == 480) { // iPhone 4
        [UIView animateWithDuration:0.3 animations:^{
            [_pleaseEnterLabel setCenter:CGPointMake(_pleaseEnterLabel.center.x, -70)];
            [_varCodeContainer setCenter:CGPointMake(_varCodeContainer.center.x, 175)];
            [_varCodeTextfield setCenter:CGPointMake(_varCodeTextfield.center.x, 175)];
            [_signinButton setCenter:CGPointMake(_signinButton.center.x, 230)];
        }];
    }
    else {
        [UIView animateWithDuration:0.3 animations:^{
            [_pleaseEnterLabel setCenter:CGPointMake(_pleaseEnterLabel.center.x, -70)];
            [_varCodeContainer setCenter:CGPointMake(_varCodeContainer.center.x, 230)];
            [_varCodeTextfield setCenter:CGPointMake(_varCodeTextfield.center.x, 230)];
            [_signinButton setCenter:CGPointMake(_signinButton.center.x, 285)];
        }];
    }
    }



@end
