//
//  SecurityViewController.h
//  Smart Community
//
//  Created by oren shany on 9/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <MapKit/MKAnnotation.h>

@interface SecurityViewController : UIViewController <MKMapViewDelegate, UITableViewDataSource, UITableViewDelegate>

@property (strong, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet MKMapView *mapView;
@property (weak, nonatomic) IBOutlet UIButton *securityButton;
@property (strong, nonatomic) IBOutlet UILabel *mapViewLabel;
@property (strong, nonatomic) IBOutlet UILabel *reportListLabel;
@property (strong, nonatomic) IBOutlet UIImageView *mapViewUnderLine;
@property (strong, nonatomic) IBOutlet UIImageView *reportListUnderline;
@property (strong, nonatomic) IBOutlet UILabel *descriptionLabel;
- (IBAction)createReport;
- (IBAction)backButtonClicked;
@property (weak, nonatomic) IBOutlet UITableView *reportsTableView;
@property (strong, nonatomic) NSArray *reportsArray;
@property (strong, nonatomic) IBOutlet UITextView *reportDesc;

@property (weak, nonatomic) IBOutlet UILabel *typeLabel;
@property (weak, nonatomic) IBOutlet UILabel *reportTitle;
@property (strong, nonatomic) IBOutlet UILabel *policeReportedLabel;
@property (weak, nonatomic) IBOutlet UIView *bottomView;

@property (nonatomic) BOOL needToOpenCreateReport;

@end
