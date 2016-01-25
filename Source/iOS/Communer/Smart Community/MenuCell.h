//
//  MenuCell.h
//  Smart Community
//
//  Created by Jukk on 8/17/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MenuCell : UITableViewCell

- (void)initWithImageAndText:(UIImage *)image andText:(NSString *)text;
@property (weak, nonatomic) IBOutlet UIImageView *cellImage;
@property (weak, nonatomic) IBOutlet UILabel *cellName;

@end
