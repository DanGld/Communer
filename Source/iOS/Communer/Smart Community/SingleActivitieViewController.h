//
//  SingleActivitieViewController.h
//  Smart Community
//
//  Created by oren shany on 8/17/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <MapKit/MKAnnotation.h>
#import "LocationModel.h"
#import "FacilityOrActivityModel.h"

@interface SingleActivitieViewController : UIViewController<UICollectionViewDataSource,UICollectionViewDelegate>
- (IBAction)backBtn;
@property (weak, nonatomic) IBOutlet UIView *calanderContainer;
@property (strong, nonatomic) IBOutlet UIScrollView *scrollView;
@property (strong, nonatomic) IBOutlet UILabel *facilityName;
@property (strong, nonatomic) IBOutlet UILabel *facilityDescription;
@property (strong, nonatomic) IBOutlet UICollectionView *imagesCollection;
@property (strong, nonatomic) IBOutlet MKMapView *mapView;
@property (strong, nonatomic) IBOutlet UIImageView *contactImage;
@property (strong, nonatomic) IBOutlet UILabel *contactName;
@property (strong, nonatomic) IBOutlet UILabel *contactPosition;
@property (strong, nonatomic) IBOutlet UILabel *contactNumber;
@property (strong, nonatomic)  NSArray* images;
@property (strong, nonatomic) LocationModel* location;
@property (strong, nonatomic) FacilityOrActivityModel* facilityObj;
-(void)setupView;

@end
