//
//  BigMemberCell.h
//  Smart Community
//
//  Created by oren shany on 06/01/2016.
//  Copyright © 2016 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface BigMemberCell : UICollectionViewCell
@property (strong, nonatomic) IBOutlet UIImageView *memberImage;
@property (strong, nonatomic) IBOutlet UILabel *memberName;
@property (strong, nonatomic) IBOutlet UILabel *memberPhone;

@end
