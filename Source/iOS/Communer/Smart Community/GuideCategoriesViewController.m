//
//  GuideCategoriesViewController.m
//  Smart Community
//
//  Created by oren shany on 8/26/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "DataManagement.h"
#import "GuideCategoriesViewController.h"
#import "SearchNewCommunityViewController.h"
#import "Networking.h"
#import "MFSideMenu.h"


@interface GuideCategoriesViewController ()

@end

@implementation GuideCategoriesViewController

NSArray* guideCats;
NSMutableArray* filteredCatsArray;
NSString* catName;
UIRefreshControl *refreshControlll;


- (void)viewDidLoad {
    [super viewDidLoad];
    NSString* obj;

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(openCommunitySearch)
                                                 name:@"openCommunitySearch"
                                               object:obj];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(reloadData)
                                                 name:@"reloadData"
                                               object:obj];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(guidesListCallBackSuccess:)
                                                 name:@"GuidesListSuccess"
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(guidesListCallBackFail)
                                                 name:@"GuidesListFail"
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(appLanguageChange)
                                                 name:@"appLanguageChange"
                                               object:obj];

    [_loaderView setAlpha:0];
    [_loader setHidesWhenStopped:YES];
    guideCats = [[LogicServices sharedInstance] getAllGuidesCategories];
    filteredCatsArray = [NSMutableArray arrayWithArray:guideCats];
    self.collectionView.delegate = self;
    self.collectionView.dataSource = self;
    [self.collectionView registerNib:[UINib nibWithNibName:@"GuideCategoryCell" bundle:[NSBundle mainBundle]]
          forCellWithReuseIdentifier:@"GuideCategoryCell"];
    
    //to add the UIRefreshControl to UIView
    refreshControlll = [[UIRefreshControl alloc] init];
    refreshControlll.attributedTitle = [[NSAttributedString alloc] initWithString:@""]; //to give the attributedTitle
    [refreshControlll addTarget:self action:@selector(refresh:) forControlEvents:UIControlEventValueChanged];
    [self.collectionView addSubview:refreshControlll];
    
    _searchBar.delegate = self;
    
    [_communityImage.layer setMasksToBounds:YES];
    [_communityImage.layer setCornerRadius:_communityImage.frame.size.width/2];
    CommunityModel* community = [[LogicServices sharedInstance] getCurrentCommunity];
    _communityName.text = community.communityName;
    [_communityImage setImageWithURL:[NSURL URLWithString:community.communityImageUrl] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
    _communityLocation.text = community.locationObj.title;
    
    [self appLanguageChange];
}

- (void)refresh:(UIRefreshControl *)refreshControl
{
    [[Networking sharedInstance] fetchNewData];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(7 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [refreshControlll endRefreshing];
    });
}


-(void)reloadData {
    guideCats = [[LogicServices sharedInstance] getAllGuidesCategories];
    filteredCatsArray = [NSMutableArray arrayWithArray:guideCats];
    CommunityModel* community = [[LogicServices sharedInstance] getCurrentCommunity];
    _communityName.text = community.communityName;
    [_communityImage setImageWithURL:[NSURL URLWithString:community.communityImageUrl] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
    _communityLocation.text = community.locationObj.title;
    [self.collectionView reloadData];
    [refreshControlll endRefreshing];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    [self.view endEditing:YES];
}

-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
        return [filteredCatsArray count];
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    GuideCategoryCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"GuideCategoryCell" forIndexPath:indexPath];
    GuideCategoryModel* guideCat = filteredCatsArray[indexPath.row];
    [cell.imageView setImageWithURL:[NSURL URLWithString:guideCat.imageUrl] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
    [cell.titleLabel setText:guideCat.title];
    return cell;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    GuideCategoryModel* clickedGuideCategory = filteredCatsArray[indexPath.row];
    catName = clickedGuideCategory.title;
    [UIView animateWithDuration:0.3 animations:^{
        [_loaderView setAlpha:0.5];
    }];
    [_loader startAnimating];
    [[Networking sharedInstance] getGuidesForCategoryName:clickedGuideCategory.title];
}

-(void)guidesListCallBackSuccess:(NSNotification *)notification {
    
    [UIView animateWithDuration:0.3 animations:^{
        [_loaderView setAlpha:0];
    }];
    [_loader stopAnimating];
    
    GuidesViewController* guidesController = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"GuidesViewController"];
    NSMutableArray* FavoriteGuidesList = notification.object;
    
    guidesController.categoryName = catName;
    guidesController.favoriteGuidesList = [NSArray array]; // for the future
    guidesController.GuidesList = FavoriteGuidesList;

    [self.navigationController pushViewController:guidesController animated:YES];
}


-(void)guidesListCallBackFail {
    [UIView animateWithDuration:0.3 animations:^{
        [_loaderView setAlpha:0];
    }];
    [_loader stopAnimating];
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Server Error" message:@"Something went wrong, try again later" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
    }
    if ([language isEqualToString:@"hebrew"]) {
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"תקלת סרבר" message:@"המידע לא התקבל בהצלחה, נסה שוב מאוחר יותר" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alert show];
    }
}

-(void)filterContentForSearchText:(NSString*)searchText scope:(NSString*)scope {
    // Update the filtered array based on the search text and scope.
    // Remove all objects from the filtered search array
    //[self.filteredCountriesArray removeAllObjects];
    // Filter the array using NSPredicate
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF.title contains[c] %@",searchText];
    filteredCatsArray = [NSMutableArray arrayWithArray:[guideCats filteredArrayUsingPredicate:predicate]];
}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText{
    // user did type something, check our datasource for text that looks the same
    if (searchText.length>0) {
        // search and reload data source
        [self filterContentForSearchText:searchText
                                   scope:[[self.searchDisplayController.searchBar scopeButtonTitles]
                                          objectAtIndex:[self.searchDisplayController.searchBar
                                                         selectedScopeButtonIndex]]];
    }
    else {
        filteredCatsArray = [NSMutableArray arrayWithArray:guideCats];
    }
    [self.collectionView performBatchUpdates:^{
        [self.collectionView reloadSections:[NSIndexSet indexSetWithIndex:0]];
    } completion:nil];
    
}


-(BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchString:(NSString *)searchString {
    // Tells the table data source to reload when text changes
    [self filterContentForSearchText:searchString scope:
     [[self.searchDisplayController.searchBar scopeButtonTitles] objectAtIndex:[self.searchDisplayController.searchBar selectedScopeButtonIndex]]];
    // Return YES to cause the search result table view to be reloaded.
    return YES;
}

-(BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchScope:(NSInteger)searchOption {
    // Tells the table data source to reload when scope bar selection changes
    [self filterContentForSearchText:self.searchDisplayController.searchBar.text scope:
     [[self.searchDisplayController.searchBar scopeButtonTitles] objectAtIndex:searchOption]];
    // Return YES to cause the search result table view to be reloaded.
    return YES;
}

-(void)openCommunitySearch {
    
    SearchNewCommunityViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SearchNewCommunityViewController"];
    [self.navigationController pushViewController:controller animated:YES];
}

- (IBAction)menuAction:(id)sender
{
    [self.menuContainerViewController setMenuState:MFSideMenuStateLeftMenuOpen completion:^{}];
}

-(void) appLanguageChange {
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
        [_tabTitle setText:@"Explore"];
        [_searchBar setPlaceholder:@"What are you looking for?"];
    }
    if ([language isEqualToString:@"hebrew"]) {
        [_tabTitle setText:@"המדריך"];
        [_searchBar setPlaceholder:@"מה מעניין אותך למצוא?"];
    }
}


@end
