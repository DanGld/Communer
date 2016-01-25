//
//  GuidesViewController.h
//  Smart Community
//
//  Created by oren shany on 8/27/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GuidesViewController : UIViewController<UITableViewDataSource,UITableViewDelegate>

@property (strong, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) IBOutlet UILabel *nameLabel;

@property (strong, nonatomic)  NSString* categoryName;
@property (strong, nonatomic)  NSArray* favoriteGuidesList;
@property (strong, nonatomic)  NSArray* GuidesList;

- (IBAction)backClicked;
@end
