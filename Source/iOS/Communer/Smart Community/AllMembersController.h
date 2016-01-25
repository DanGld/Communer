//
//  AllMembersController.h
//  Smart Community
//
//  Created by oren shany on 06/01/2016.
//  Copyright © 2016 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AllMembersController : UIViewController <UICollectionViewDelegate,UICollectionViewDataSource,UISearchBarDelegate,UISearchControllerDelegate>
@property (strong, nonatomic) NSMutableArray* membersArr;
@property (strong, nonatomic) IBOutlet UISearchBar *searchBar;
@property (strong, nonatomic) IBOutlet UICollectionView *membersCollection;

@end
