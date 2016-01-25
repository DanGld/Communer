//
//  CalenderEventCell.m
//  Smart Community
//
//  Created by oren shany on 8/5/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "CalenderEventCell.h"
#import "EventOrMessageModel.h"

@implementation CalenderEventCell

- (void)awakeFromNib {
    [self.layer setMasksToBounds:YES];
    [self.layer setCornerRadius:3.0f];
    [self.bgLabel.layer setMasksToBounds:YES];
    [self.bgLabel.layer setCornerRadius:5.0f];
    [self setSelectionStyle:UITableViewCellSelectionStyleNone];
    _bgImage.userInteractionEnabled = YES;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}


@end
