//
//  SignInViewController.h
//  Smart Community
//
//  Created by oren shany on 7/23/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KNCirclePercentView.h"

@interface SignInViewController : UIViewController<UITextFieldDelegate>

@property (strong, nonatomic) IBOutlet UIButton *codeLabel;
@property (strong, nonatomic) IBOutlet UITextField *phoneTextfield;
@property (strong, nonatomic) IBOutlet UILabel *toSignLabel;
@property (strong, nonatomic) IBOutlet UIView *signView;

@property (strong, nonatomic) IBOutlet UIView *verifyView;
@property (strong, nonatomic) IBOutlet UIButton *signupButton;
@property (strong, nonatomic) IBOutlet UILabel *verifyDetailLabel;
@property (strong, nonatomic) IBOutlet KNCirclePercentView *verifyTimerBgView;
@property (strong, nonatomic) IBOutlet KNCirclePercentView *verifyTimerView;

@property (strong, nonatomic) IBOutlet UIView *codeView;
@property (strong, nonatomic) IBOutlet UIImageView *varcodeContainer;
@property (strong, nonatomic) IBOutlet UITextField *varcodeTextfield;
@property (strong, nonatomic) IBOutlet UIActivityIndicatorView *loader;

- (IBAction)onSignUpAction;
@end
