//
//  GuideCategoryCell.m
//  Smart Community
//
//  Created by oren shany on 8/26/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "GuideCategoryCell.h"

@implementation GuideCategoryCell

- (void)awakeFromNib {
    [_cellView.layer setMasksToBounds:YES];
    [_cellView.layer setCornerRadius:28];
}

@end
