//
//  SignInViewController.h
//  Smart Community
//
//  Created by oren shany on 7/23/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SignInViewController : UIViewController<UITextFieldDelegate>

@property (strong, nonatomic) IBOutlet UIButton *codeLabel;
@property (strong, nonatomic) IBOutlet UITextField *phoneTextfield;
@property (strong, nonatomic) IBOutlet UILabel *toSignLabel;
@property (strong, nonatomic) IBOutlet UIView *signView;

- (IBAction)signInButton;

@end
