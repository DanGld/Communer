//
//  nextServicesCell.m
//  Smart Community
//
//  Created by oren shany on 7/28/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "nextServicesCell.h"

@implementation nextServicesCell

- (void)awakeFromNib {
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
        [_firstPrayerName setText:@"Shacharit"];
        [_secondPrayerName setText:@"Mincha"];
        [_thirdPrayer setText:@"Maariv"];
    }
    if ([language isEqualToString:@"hebrew"]) {
        [_firstPrayerName setText:@"שחרית"];
        [_secondPrayerName setText:@"מנחה"];
        [_thirdPrayerName setText:@"מעריב"];
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
