//
//  SingleActivitieViewController.m
//  Smart Community
//
//  Created by oren shany on 8/17/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "SingleActivitieViewController.h"
#import "UIImageView+AFNetworking.h"
#import "LogicServices.h"
#import "ImageCollectionCell.h"
#import "NextEventsCell.h"

@interface SingleActivitieViewController ()

@end

@implementation SingleActivitieViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [_contactImage.layer setMasksToBounds:YES];
    [_contactImage.layer setCornerRadius:25.0f];
    
    
    
    [_scrollView setContentSize:CGSizeMake([UIScreen mainScreen].bounds.size.width, 720)];
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NextEventsCell *calander = [[[NSBundle mainBundle] loadNibNamed:@"NextEventsCell" owner:self options:nil] objectAtIndex:0];
    [calander setFrame:_calanderContainer.frame];
    [_calanderContainer addSubview:calander];
}

-(void)setupView {
    _facilityName.text = _facilityObj.name;
    _facilityDescription.text = _facilityObj.descr;
    [_imagesCollection registerNib:[UINib nibWithNibName:@"ImageCollectionCell" bundle:[NSBundle mainBundle]]
                      forCellWithReuseIdentifier:@"ImageCollectionCell"];
    [_imagesCollection setDelegate:self];
    [_imagesCollection setDataSource:self];
    
    if (![_facilityObj.location.coords isKindOfClass:[NSNull class]] && _facilityObj.location.coords.length>0)
    [_mapView setCenterCoordinate:CLLocationCoordinate2DMake([[_facilityObj.location.coords componentsSeparatedByString:@","][0] doubleValue], [[_facilityObj.location.coords componentsSeparatedByString:@","][1]doubleValue]) animated:YES];
    
    if (![_facilityObj.communityContact.imageUrl isKindOfClass:[NSNull class]])
    [_contactImage setImageWithURL:[NSURL URLWithString:_facilityObj.communityContact.imageUrl] placeholderImage:[UIImage imageNamed:@"communer_placeholder"]];
    
    _contactName.text = _facilityObj.communityContact.name;
    _contactPosition.text = _facilityObj.communityContact.position;
    _contactNumber.text = _facilityObj.communityContact.phoneNumber;
}


- (IBAction)backBtn {
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}

-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return _facilityObj.images.count;
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    ImageCollectionCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"ImageCollectionCell" forIndexPath:indexPath];
    
    NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:_facilityObj.images[indexPath.row]]
                                                  cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                              timeoutInterval:60];
    
    [cell.imageView setImageWithURLRequest:imageRequest
                          placeholderImage:[UIImage imageNamed:@"placeHolder"]
                                   success:nil
                                   failure:nil];
    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath{
    return CGSizeMake(125, 100);
}

@end
