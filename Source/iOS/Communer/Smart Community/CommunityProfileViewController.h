//
//  CommunityProfileViewController.h
//  Smart Community
//
//  Created by oren shany on 9/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MessageUI/MFMailComposeViewController.h>

@interface CommunityProfileViewController : UIViewController<UICollectionViewDataSource,UICollectionViewDelegate,MFMailComposeViewControllerDelegate>
@property (strong, nonatomic) IBOutlet UILabel *titleLabel;
@property (strong, nonatomic) IBOutlet UIScrollView *scrollView;
@property (strong, nonatomic) IBOutlet UIView *viewInScroll;
@property (strong, nonatomic) IBOutlet UIImageView *communityImage;
@property (strong, nonatomic) IBOutlet UILabel *communityNameLabel;
@property (strong, nonatomic) IBOutlet UICollectionView *communityMembersCollection;
@property (strong, nonatomic) IBOutlet UIWebView *communityLocationWebview;
@property (strong, nonatomic) IBOutlet UIImageView *communityContactImage;
@property (strong, nonatomic) IBOutlet UILabel *communityContactNameLabel;
@property (strong, nonatomic) IBOutlet UILabel *communityContactPositionLabel;
@property (strong, nonatomic) IBOutlet UILabel *communityContactPhoneLabel;
@property (strong, nonatomic) IBOutlet UILabel *communityFaxLabel;
@property (strong, nonatomic) IBOutlet UILabel *communityMailLabel;
@property (strong, nonatomic) IBOutlet UILabel *communityWebsiteLabel;
@property (strong, nonatomic) IBOutlet UIButton *beMemberButton;
@property (strong, nonatomic) IBOutlet UIButton *leaveCommunityButton;
@property (strong, nonatomic) IBOutlet UILabel *locationTitleLabel;
@property (strong, nonatomic) IBOutlet UILabel *nameLabel;
@property (strong, nonatomic) IBOutlet UILabel *membersLabel;
@property (strong, nonatomic) IBOutlet UILabel *locationLabel;
@property (strong, nonatomic) IBOutlet UILabel *contactDetailsLabel;
@property (strong, nonatomic) IBOutlet UILabel *faxLabel;
@property (strong, nonatomic) IBOutlet UILabel *emailLabel;
@property (strong, nonatomic) IBOutlet UILabel *websiteLabel;

@property (strong, nonatomic) IBOutlet UIActivityIndicatorView *loader;
@property (strong, nonatomic) IBOutlet UIView *loaderView;
- (IBAction)callCommunityContact;
- (IBAction)applyToBeMember;
- (IBAction)leaveCommunityClicked;

@end
