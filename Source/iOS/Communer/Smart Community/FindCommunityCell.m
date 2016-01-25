//
//  FindCommunityCell.m
//  Smart Community
//
//  Created by oren shany on 9/1/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "FindCommunityCell.h"

@implementation FindCommunityCell

- (void)awakeFromNib {
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
        [_titleLabel setText:@"Find Community"];
    }
    if ([language isEqualToString:@"hebrew"]) {
        [_titleLabel setText:@"מצא קהילה"];
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
