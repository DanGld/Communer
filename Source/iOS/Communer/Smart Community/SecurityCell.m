//
//  SecurityCell.m
//  Smart Community
//
//  Created by Eyal Makover on 9/24/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "SecurityCell.h"

@implementation SecurityCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    // Configure the view for the selected state
}

- (void)initWithReport:(SecurityEventModel*)event {
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];

    if (event.report.type && ![event.report.type isEqualToString:@""]) {
        _reportType.text = event.report.type;
    }
    else {
        _reportType.text = @"No Type";
        if ([language isEqualToString:@"hebrew"]) {
            [_reportType setText:@"לא מסווג"];
        }
    }
    
    if ([language isEqualToString:@"english"]) {
         _reportLocation.text = [NSString stringWithFormat:@"at %@", event.report.location.title];
        _reporterName.text = event.reporterName.length>0 ? event.reporterName : @"annonymous";
    }
    if ([language isEqualToString:@"hebrew"]) {
        _reportLocation.text = [NSString stringWithFormat:@"ב %@", event.report.location.title];
        _reporterName.text = event.reporterName.length>0 ? event.reporterName : @"אנונימי";
        [_hasbeenReprotedLabel setText:@"דווח על ידי"];
        [_readMoreLabel setText:@"קרא עוד"];
    }
    if ([event.report.severity isEqualToString:@"low"]) {
        [_securityIcon setImage:[UIImage imageNamed:@"green_report"]];
    }
    if ([event.report.severity isEqualToString:@"medium"]) {
        [_securityIcon setImage:[UIImage imageNamed:@"yellow_report"]];
    }
    if ([event.report.severity isEqualToString:@"high"]) {
        [_securityIcon setImage:[UIImage imageNamed:@"red_report"]];
    }
}

@end
