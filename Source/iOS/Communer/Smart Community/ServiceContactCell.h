//
//  ServiceContactCell.h
//  Smart Community
//
//  Created by oren shany on 8/27/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ServiceContactCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UIImageView *contactImage;
@property (strong, nonatomic) IBOutlet UILabel *contactName;
@property (strong, nonatomic) IBOutlet UILabel *contactAddress;
@property (strong, nonatomic) IBOutlet UILabel *contactPhoneNumber;
@property (strong, nonatomic) IBOutlet UILabel *contactRating;

@property (strong, nonatomic) IBOutlet UIImageView *star1;
@property (strong, nonatomic) IBOutlet UIImageView *star2;
@property (strong, nonatomic) IBOutlet UIImageView *star3;
@property (strong, nonatomic) IBOutlet UIImageView *star4;
@property (strong, nonatomic) IBOutlet UIImageView *star5;

-(void)setStars;

@end
