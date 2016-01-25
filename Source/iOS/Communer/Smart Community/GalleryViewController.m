//
//  GalleryViewController.m
//  Smart Community
//
//  Created by oren shany on 8/5/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "GalleryViewController.h"
#import "SingleGalleryViewController.h"
#import "GalleryCell.h"
#import "LogicServices.h"
#import "UIImageView+AFNetworking.h"
#import "SearchNewCommunityViewController.h"
#import "Networking.h"



@interface GalleryViewController ()

@end

@implementation GalleryViewController

DataManagement* dataManeger;
NSArray* imagesEvents;
UIRefreshControl *refreshControll;

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
                                             selector:@selector(appLanguageChange)
                                                 name:@"appLanguageChange"
                                               object:obj];

    
     dataManeger = [DataManagement sharedInstance];
    _galleryTable.delegate = self;
    _galleryTable.dataSource = self;
    
    //to add the UIRefreshControl to UIView
    refreshControll = [[UIRefreshControl alloc] init];
    refreshControll.attributedTitle = [[NSAttributedString alloc] initWithString:@""]; //to give the attributedTitle
    [refreshControll addTarget:self action:@selector(refresh:) forControlEvents:UIControlEventValueChanged];
    [self.galleryTable addSubview:refreshControll];
    
    [_communityImage.layer setMasksToBounds:YES];
    [_communityImage.layer setCornerRadius:_communityImage.frame.size.width/2];
    CommunityModel* community = [[LogicServices sharedInstance] getCurrentCommunity];
    _communityName.text = community.communityName;
    [_communityImage setImageWithURL:[NSURL URLWithString:community.communityImageUrl] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
    _communityLoaction.text = community.locationObj.title;
    
    imagesEvents = [[LogicServices sharedInstance] getAllImagesEvents];
    
    [self appLanguageChange];
}

- (void)refresh:(UIRefreshControl *)refreshControl
{
    [[Networking sharedInstance] fetchNewData];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(7 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [refreshControll endRefreshing];
    });
}

-(void)reloadData {
    CommunityModel* community = [[LogicServices sharedInstance] getCurrentCommunity];
    _communityName.text = community.communityName;
    [_communityImage setImageWithURL:[NSURL URLWithString:community.communityImageUrl] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
    _communityLoaction.text = community.locationObj.title;
    imagesEvents = [[LogicServices sharedInstance] getAllImagesEvents];
    [self.galleryTable reloadData];
    [refreshControll endRefreshing];
}



-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return imagesEvents.count;
}


-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    GalleryCell *cell = [tableView dequeueReusableCellWithIdentifier:@"GalleryCell"];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"GalleryCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    
    EventMediaModel* eventMedia = imagesEvents[indexPath.row];
    NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:eventMedia.imageUrl]
                                                  cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                              timeoutInterval:60];
    
    [cell.img setImageWithURLRequest:imageRequest
                     placeholderImage:[UIImage imageNamed:@"placeHolder"]
                              success:nil
                              failure:nil];

    cell.galleryTitle.text = eventMedia.eventTitle;
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    SingleGalleryViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SingleGalleryViewController"];
    EventMediaModel* eventMedia = imagesEvents[indexPath.row];
    controller.images = eventMedia.media;
    controller.galleryTitle = eventMedia.eventTitle;
    [self presentViewController:controller animated:YES completion:nil];
}

- (IBAction)menuAction:(id)sender
{
    [self.menuContainerViewController setMenuState:MFSideMenuStateLeftMenuOpen completion:^{}];
}

-(void)openCommunitySearch {
    [self.menuContainerViewController setMenuState:MFSideMenuStateClosed];
    
    SearchNewCommunityViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SearchNewCommunityViewController"];
    [self.navigationController pushViewController:controller animated:YES];
}

-(void) appLanguageChange {
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
        [_tabTitle setText:@"Gallery"];
    }
    if ([language isEqualToString:@"hebrew"]) {
        [_tabTitle setText:@"גלריה"];
    }
}


@end
