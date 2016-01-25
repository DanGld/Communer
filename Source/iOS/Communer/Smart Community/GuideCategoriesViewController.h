//
//  GuideCategoriesViewController.h
//  Smart Community
//
//  Created by oren shany on 8/26/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LogicServices.h"
#import "GuideCategoryCell.h"
#import "UIImageView+AFNetworking.h"
#import "GuidesViewController.h"
#import "ServiceContactModel.h"

@interface GuideCategoriesViewController : UIViewController<UICollectionViewDataSource,UICollectionViewDelegate,UISearchBarDelegate,UISearchControllerDelegate>

@property (strong, nonatomic) IBOutlet UILabel *tabTitle;
@property (strong, nonatomic) IBOutlet UICollectionView *collectionView;
@property (weak, nonatomic) IBOutlet UISearchBar *searchBar;
@property (weak, nonatomic) IBOutlet UIImageView *communityImage;
@property (weak, nonatomic) IBOutlet UILabel *communityName;
@property (weak, nonatomic) IBOutlet UILabel *communityLocation;
@property (weak, nonatomic) IBOutlet UIView *loaderView;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *loader;

@end
