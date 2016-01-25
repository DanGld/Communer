//
//  VarificationViewController.h
//  Smart Community
//
//  Created by oren shany on 7/23/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Google/SignIn.h>


@interface VarificationViewController : UIViewController<UITextFieldDelegate,GIDSignInUIDelegate>
@property (strong, nonatomic) IBOutlet UIView *loginPopupView;
@property (strong, nonatomic) IBOutlet UIView *popupBlackView;
@property (strong, nonatomic) IBOutlet UIActivityIndicatorView *loader;
@property (strong, nonatomic) IBOutlet UILabel *pleaseEnterLabel;
@property (strong, nonatomic) IBOutlet UIButton *signinButton;
@property (strong, nonatomic) IBOutlet UIImageView *varCodeContainer;
@property (strong, nonatomic) IBOutlet UITextField *varCodeTextfield;
@property(weak, nonatomic) IBOutlet GIDSignInButton *googleSignInButton;



- (IBAction)signInButton;
- (void)getDataCallback;
- (IBAction)facebookSignUp;

@end
