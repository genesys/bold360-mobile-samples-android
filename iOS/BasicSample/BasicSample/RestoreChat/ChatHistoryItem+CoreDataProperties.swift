//
//  ChatHistoryItem+CoreDataProperties.swift
//  BasicSample
//
//  Created by Nissim Pardo on 29/04/2019.
//  Copyright © 2019 bold360ai. All rights reserved.
//
//

import Foundation
import CoreData


extension ChatHistoryItem {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<ChatHistoryItem> {
        return NSFetchRequest<ChatHistoryItem>(entityName: "ChatHistoryItem")
    }

    @NSManaged public var isRemovable: Bool
    @NSManaged public var itemId: Int64
    @NSManaged public var itemSource: Int16
    @NSManaged public var itemStatus: Int16
    @NSManaged public var itemText: String?
    @NSManaged public var itemType: Int16
    @NSManaged public var json: String?
    @NSManaged public var scope: Int16
    @NSManaged public var timeStamp: NSDate?
    @NSManaged public var config: ChatConfiguration?

}
