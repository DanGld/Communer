//
//  CountryPickingViewController.h
//  Smart Community
//
//  Created by oren shany on 7/23/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CountryPickingViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,UISearchBarDelegate,UISearchControllerDelegate>

@property (strong, nonatomic) IBOutlet UITableView *countriesTable;
@property (strong, nonatomic) IBOutlet UISearchBar *searchBar;

@property (strong,nonatomic) NSMutableArray *filteredCountriesArray;
- (IBAction)backBtn;

@end
