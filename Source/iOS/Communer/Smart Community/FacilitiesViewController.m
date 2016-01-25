//
//  FacilitiesViewController.m
//  Smart Community
//
//  Created by oren shany on 8/31/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "FacilitiesViewController.h"
#import "LogicServices.h"
#import "FacilityCell.h"
#import "SingleActivitieViewController.h"
#import "UIImageView+AFNetworking.h"


@interface FacilitiesViewController ()

@end

@implementation FacilitiesViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.table setDelegate:self];
    [self.table setDataSource:self];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return [LogicServices sharedInstance].getAllCommunityFacilities.count;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 247;
}

-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 40;
}

- (UIView *) tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.table.bounds.size.width, 40)];
    [headerView setBackgroundColor:[UIColor clearColor]];
    return headerView;
}


-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    FacilityCell *cell = [tableView dequeueReusableCellWithIdentifier:@"FacilityCell"];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"FacilityCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    FacilityOrActivityModel* facility = [LogicServices sharedInstance].getAllCommunityFacilities[indexPath.section];
    cell.facilityName.text = facility.name;
    [cell.facilityImage setImageWithURL:[NSURL URLWithString:facility.bigImageUrl] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    SingleActivitieViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SingleActivitieViewController"];
    controller.facilityObj =[LogicServices sharedInstance].getAllCommunityFacilities[indexPath.section];
    [self presentViewController:controller animated:YES completion:nil];
    [controller setupView];
}

- (IBAction)backClicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
