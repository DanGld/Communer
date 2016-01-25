//
//  BigMemberCell.m
//  Smart Community
//
//  Created by oren shany on 06/01/2016.
//  Copyright © 2016 Moveo. All rights reserved.
//

#import "BigMemberCell.h"

@implementation BigMemberCell

- (void)awakeFromNib {
    [_memberImage.layer setMasksToBounds:YES];
    [_memberImage.layer setCornerRadius:38.0f];
}

@end
