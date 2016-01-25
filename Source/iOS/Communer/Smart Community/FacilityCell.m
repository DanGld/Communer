//
//  FacilityCell.m
//  Smart Community
//
//  Created by oren shany on 8/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "FacilityCell.h"

@implementation FacilityCell

- (void)awakeFromNib {
   // self.imageView.layer.shadowColor = [UIColor purpleColor].CGColor;
   // self.imageView.layer.shadowOffset = CGSizeMake(0, 1);
    //self.imageView.layer.shadowOpacity = 1;
    //self.imageView.layer.shadowRadius = 1.0;
    //self.imageView.clipsToBounds = NO;
    [self.imageView.layer setMasksToBounds:YES];
}


- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
