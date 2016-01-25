//
//  EventMediaModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"

@interface EventMediaModel : KVCBaseObject

@property (nonatomic, strong) NSString* galleryID;
@property (nonatomic, strong) NSString* imageUrl;
@property (nonatomic, strong) NSString* eventTitle;
@property (nonatomic, strong) NSMutableArray* media;
@property (nonatomic) BOOL isActive;

@end
