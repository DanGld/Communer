//
//  CommunityModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LocationModel.h"
#import "CommunityMemberModel.h"
#import "KVCBaseObject.h"

@interface CommunityModel : KVCBaseObject

@property (nonatomic, strong) NSString* communityID;
@property (nonatomic, strong) NSMutableArray* messages;
@property (nonatomic, strong) NSString* communityName;
@property (nonatomic, strong) NSString* communityImageUrl;
@property (nonatomic, strong) LocationModel* locationObj;
@property (nonatomic, strong) NSString* memberType;
@property (nonatomic, strong) CommunityModel* roofOrg;
@property (nonatomic) BOOL isSynagogue;
@property (nonatomic, strong) NSString* communityType;
@property (nonatomic, strong) NSMutableArray* members;
@property (nonatomic, strong) NSMutableArray* contacts;
@property (nonatomic, strong) NSMutableArray* communityContacts;
@property (nonatomic, strong) NSString* fax;
@property (nonatomic, strong) NSString* email;
@property (nonatomic, strong) NSString* website;
@property (nonatomic, strong) NSMutableArray* privateQA;
@property (nonatomic, strong) NSMutableArray* publicQA;
@property (nonatomic, strong) NSMutableArray* communityPosts;
@property (nonatomic, strong) NSMutableArray* facilities;
@property (nonatomic, strong) NSMutableArray* dailyEvents;
@property (nonatomic, strong) NSMutableArray* eventImages;
@property (nonatomic, strong) NSMutableArray* securityEvents;
@property (nonatomic, strong) NSMutableArray* guideCategories;
@property (nonatomic) BOOL hebrewSupport;
@property (nonatomic, strong) NSMutableArray* visibleModules;
@property (nonatomic, strong) NSMutableArray* widgets;
@property (nonatomic, strong) NSMutableArray* pushTags;
@property (nonatomic, strong) NSMutableArray* pushChannels;


@end
