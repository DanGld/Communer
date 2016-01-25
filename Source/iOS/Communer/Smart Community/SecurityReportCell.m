//
//  SecurityReportCell.m
//  Smart Community
//
//  Created by oren shany on 7/30/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "SecurityReportCell.h"

@implementation SecurityReportCell

- (void)awakeFromNib {
    NSString* appLanguage = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([appLanguage isEqualToString:@"hebrew"]) {
        [_viewReportsLabel setText:@"ראה דיווחים"];
        [_createReportLabel setText:@"דווח"];
    }
}

- (IBAction)ViewReportsClicked {
    [[NSNotificationCenter defaultCenter] postNotificationName:@"openSecurityReports"
                                                        object:nil
                                                      userInfo:nil];

}

- (IBAction)createReportClicked {
    [[NSNotificationCenter defaultCenter] postNotificationName:@"createSecurityReports"
                                                        object:nil
                                                      userInfo:nil];
}


@end
