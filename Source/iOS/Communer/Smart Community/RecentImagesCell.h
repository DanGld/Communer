//
//  RecentImagesCell.h
//  Smart Community
//
//  Created by oren shany on 7/30/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RecentImagesCell : UITableViewCell<UICollectionViewDataSource,UICollectionViewDelegate>
@property (strong, nonatomic) IBOutlet UICollectionView *collectionView;

@end
