//
//  FacilitiesViewController.h
//  Smart Community
//
//  Created by oren shany on 8/31/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FacilitiesViewController : UIViewController<UITableViewDataSource,UITableViewDelegate>
@property (strong, nonatomic) IBOutlet UITableView *table;

@end
