//
//  GalleryViewController.h
//  Smart Community
//
//  Created by oren shany on 8/5/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MFSideMenu.h"

@interface GalleryViewController : UIViewController<UITableViewDataSource,UITableViewDelegate>
@property (strong, nonatomic) IBOutlet UILabel *tabTitle;
@property (strong, nonatomic) IBOutlet UITableView *galleryTable;
@property (strong, nonatomic) IBOutlet UILabel *communityName;
@property (strong, nonatomic) IBOutlet UIImageView *communityImage;
@property (strong, nonatomic) IBOutlet UILabel *communityLoaction;
@property(nonatomic)MFSideMenuContainerViewController *container;
- (IBAction)menuAction:(id)sender;

@end
