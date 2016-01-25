//
//  ServiceCell.h
//  Smart Community
//
//  Created by Eyal Makover on 11/1/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ServiceCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *serviceTitle;
@property (weak, nonatomic) IBOutlet UILabel *serviceTimes;

@end
