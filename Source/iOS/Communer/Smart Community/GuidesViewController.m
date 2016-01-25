//
//  GuidesViewController.m
//  Smart Community
//
//  Created by oren shany on 8/27/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "GuidesViewController.h"
#import "ServiceContactCell.h"
#import "UIImageView+AFNetworking.h"
#import "ServiceContactModel.h"
#import "FavoriteServiceContactCell.h"
#import "GuideViewController.h"

@interface GuidesViewController ()

@end

@implementation GuidesViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    
    [self.nameLabel setText:_categoryName];
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section==0) {
        return self.favoriteGuidesList.count;
    }
    else {
        return self.GuidesList.count;
    }
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section==0){
        FavoriteServiceContactCell *cell = [tableView dequeueReusableCellWithIdentifier:@"FavoriteServiceContactCell"];
        if (cell == nil) {
            // Load the top-level objects from the custom cell XIB.
            NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"FavoriteServiceContactCell" owner:self options:nil];
            // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
            cell = [topLevelObjects objectAtIndex:0];
        }
        
        ServiceContactModel* serviceContact =  _favoriteGuidesList[indexPath.row];
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
        [cell.contactImage setImageWithURL:[NSURL URLWithString:serviceContact.imageUrl]  placeholderImage:[UIImage imageNamed:@"placeHolder"]];
        [cell.contactName setText:serviceContact.name];
        [cell.contactAddress setText:serviceContact.location.title];
        [cell.contactPhoneNumber setText:serviceContact.phoneNumber];
        [cell.contactRating setText:[NSString stringWithFormat:@"%.1f" , serviceContact.rating]];
        [cell setStars];
        return cell;
    }
    else {
    ServiceContactCell *cell = [tableView dequeueReusableCellWithIdentifier:@"ServiceContactCell"];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"ServiceContactCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
    
    ServiceContactModel* serviceContact =  _GuidesList[indexPath.row];
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
        
        NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:serviceContact.imageUrl]
                                                      cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                                  timeoutInterval:60];
        
        [cell.contactImage setImageWithURLRequest:imageRequest
                      placeholderImage:[UIImage imageNamed:@"noprofilepic.jpg"]
                               success:nil
                               failure:nil];
        
        [cell.contactName setText:serviceContact.name];
        [cell.contactAddress setText:serviceContact.location.title];
        [cell.contactPhoneNumber setText:serviceContact.phoneNumber];
        [cell.contactRating setText:[NSString stringWithFormat:@"%.1f" , serviceContact.rating]];
        [cell setStars];
        return cell;
    }
    
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    GuideViewController* guideController = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"GuideViewController"];
    if (indexPath.section == 0) {
        guideController.serviceContact =  _favoriteGuidesList[indexPath.row];
        guideController.guideTitle = _categoryName;
    }
    else {
        guideController.serviceContact =  _GuidesList[indexPath.row];
        guideController.guideTitle = _categoryName;
        }
    [self.navigationController pushViewController:guideController animated:YES];
}

- (IBAction)backClicked {
    [self.navigationController popViewControllerAnimated:YES];
}


@end
