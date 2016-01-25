//
//  AllMembersController.m
//  Smart Community
//
//  Created by oren shany on 06/01/2016.
//  Copyright © 2016 Moveo. All rights reserved.
//

#import "AllMembersController.h"
#import "BigMemberCell.h"
#import "CommunityMemberModel.h"
#import "UIImageView+AFNetworking.h"

@interface AllMembersController ()

@end

@implementation AllMembersController
NSMutableArray* filteredMembersArr;

- (void)viewDidLoad {
    [super viewDidLoad];
    filteredMembersArr = [NSMutableArray arrayWithArray:_membersArr];
    
    [_membersCollection setDelegate:self];
    [_membersCollection setDataSource:self];
    [_membersCollection registerNib:[UINib nibWithNibName:@"BigMemberCell" bundle:[NSBundle mainBundle]] forCellWithReuseIdentifier:@"BigMemberCell"];
    [_membersCollection reloadData];
    [_searchBar setDelegate:self];
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
        return CGSizeMake(100, 109);
}

-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return filteredMembersArr.count;
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    BigMemberCell* cell = (BigMemberCell*)[_membersCollection dequeueReusableCellWithReuseIdentifier:@"BigMemberCell" forIndexPath:indexPath];
    CommunityMemberModel* member = filteredMembersArr[indexPath.item];
    NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:member.imageUrl]
                                                  cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                              timeoutInterval:60];
    [cell.memberImage setImageWithURLRequest:imageRequest
                           placeholderImage:[UIImage imageNamed:@"communer_placeholder"]
                                    success:nil
                                    failure:nil];

    
    [cell.memberName setText:member.name];
    [cell.memberPhone setText:member.phone];
    if ([UIScreen mainScreen].bounds.size.height<570) {
        [cell.memberName setFont:[UIFont systemFontOfSize:15.0f]];
    }
    return cell;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    [self.view endEditing:YES];
}

-(void)scrollViewDidScroll:(UIScrollView *)scrollView {
    [self.view endEditing:YES];
}

-(void)filterContentForSearchText:(NSString*)searchText scope:(NSString*)scope {
    // Update the filtered array based on the search text and scope.
    // Remove all objects from the filtered search array
    //[self.filteredCountriesArray removeAllObjects];
    // Filter the array using NSPredicate
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF.name MATCHES[c] %@",[NSString stringWithFormat: @".*\\b%@.*",searchText]];
    filteredMembersArr = [NSMutableArray arrayWithArray:[_membersArr filteredArrayUsingPredicate:predicate]];
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
        filteredMembersArr = [NSMutableArray arrayWithArray:_membersArr];
    }
    [_membersCollection performBatchUpdates:^{
        [_membersCollection reloadSections:[NSIndexSet indexSetWithIndex:0]];
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


- (IBAction)backClicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}


@end
