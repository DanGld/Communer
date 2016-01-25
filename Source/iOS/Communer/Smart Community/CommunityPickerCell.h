//
//  CommunityPickerCell.h
//  Smart Community
//
//  Created by oren shany on 9/1/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CommunityPickerCell : UITableViewCell
@property (strong, nonatomic) IBOutlet UIImageView *communityImage;
@property (strong, nonatomic) IBOutlet UILabel *communityName;
@property (strong, nonatomic) IBOutlet UILabel *communityLocation;
@property (strong, nonatomic) IBOutlet UILabel *communityType;

@end
