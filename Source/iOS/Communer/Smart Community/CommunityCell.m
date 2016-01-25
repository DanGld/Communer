//
//  CommunityCell.m
//  Smart Community
//
//  Created by oren shany on 9/1/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "CommunityCell.h"

@implementation CommunityCell

- (void)awakeFromNib {
    [_communityImage.layer setCornerRadius:17.5];
    [_communityImage.layer setMasksToBounds:YES];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
