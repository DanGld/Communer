//
//  GuideCategoryModel.h
//  Smart Community
//
//  Created by oren shany on 8/26/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"

@interface GuideCategoryModel : KVCBaseObject

@property (nonatomic, strong) NSString* title;
@property (nonatomic, strong) NSString* imageUrl;

@end
