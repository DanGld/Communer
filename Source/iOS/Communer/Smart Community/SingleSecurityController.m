//
//  SingleSecurityController.m
//  Smart Community
//
//  Created by oren shany on 11/10/15.
//  Copyright © 2015 Moveo. All rights reserved.
//

#import "SingleSecurityController.h"
#import "SecurityEventModel.h"


@interface SingleSecurityController ()

@end

@implementation SingleSecurityController

- (void)viewDidLoad {
    [super viewDidLoad];
}

-(void)initWithReport:(SecurityEventModel*)report {
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if (report.report.type && ![report.report.type isEqualToString:@""])
        [_reportType setText:report.report.type];
    else {
        [_reportType setText:@"No Type"];
        
        if ([language isEqualToString:@"hebrew"]) {
            [_reportType setText:@"לא מסווג"];
        }
    }
    
    if ([language isEqualToString:@"english"]) {
        [_reportTitle setText:[NSString stringWithFormat:@"Has been reported by %@ at %@" , report.reporterName.length>0 ? report.reporterName : @"annonymous", report.report.location.title]];
    [_reportDesc setText:report.report.desc];
        _policeReport.text = report.report.isPoliceReport ? @"The police have been reported" : @"The police haven't been reported yet";
    }
    if ([language isEqualToString:@"hebrew"]) {
        _reportTitle.text = [NSString stringWithFormat:@"דווח על ידי %@ ב %@" , report.reporterName.length>0 ? report.reporterName : @"אנונימי" , report.report.location.title];
        _reportDesc.text = report.report.desc;
        _policeReport.text = report.report.isPoliceReport ? @"המשטרה קיבלה דיווח" : @"המשטרה לא קיבלה דיווח";
        [_descLabel setText:@"תיאור"];
    }
    
    CGSize theStringSize = [self.reportDesc.text sizeWithFont:[UIFont fontWithName:@"Roboto" size:14.0f] constrainedToSize:CGSizeMake(_reportDesc.frame.size.width, _reportDesc.frame.size.height)];
    if (theStringSize.height>_reportDesc.frame.size.height) {
        [_reportDesc sizeToFit];
    }
    
}

- (IBAction)backClicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
