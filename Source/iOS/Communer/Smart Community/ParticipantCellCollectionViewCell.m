//
//  ParticipantCellCollectionViewCell.m
//  Smart Community
//
//  Created by oren shany on 8/30/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "ParticipantCellCollectionViewCell.h"

@implementation ParticipantCellCollectionViewCell

- (void)awakeFromNib {
    [_imageView.layer setCornerRadius:25];
    [_imageView.layer setMasksToBounds:YES];
}

@end
