//
//  SingleSecurityController.h
//  Smart Community
//
//  Created by oren shany on 11/10/15.
//  Copyright © 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SecurityEventModel.h"

@interface SingleSecurityController : UIViewController
@property (strong, nonatomic) IBOutlet UILabel *reportType;
@property (strong, nonatomic) IBOutlet UILabel *reportTitle;
@property (strong, nonatomic) IBOutlet UITextView *reportDesc;
@property (strong, nonatomic) IBOutlet UILabel *policeReport;
@property (strong, nonatomic) IBOutlet UILabel *descLabel;

-(void)initWithReport:(SecurityEventModel*)report;
- (IBAction)backClicked;

@end
