package main.types;

import lombok.Getter;

public enum Items {
	NONE(0),
	BRONZE_HELM(1),
	BROADSWORD(2),
	COPPER_ORE(3),
	IRON_ORE(4),
	COAL_ORE(5),
	MITHRIL_ORE(6),
	COPPER_HELMET(7),
	COPPER_PLATEBODY(8),
	COPPER_PLATELEGS(9),
	COPPER_SHIELD(10),
	
	IRON_HELMET(14),
	IRON_PLATEBODY(15),
	IRON_PLATELEGS(16),
	IRON_SHIELD(17),
	
	STEEL_HELMET(21),
	STEEL_PLATEBODY(22),
	STEEL_PLATELEGS(23),
	STEEL_SHIELD(24),
	
	MITHRIL_HELMET(28),
	MITHRIL_PLATEBODY(29),
	MITHRIL_PLATELEGS(30),
	MITHRIL_SHIELD(31),
	
	ADDY_HELMET(35),
	ADDY_PLATEBODY(36),
	ADDY_PLATELEGS(37),
	ADDY_SHIELD(38),
	
	RUNE_HELMET(42),
	RUNE_PLATEBODY(43),
	RUNE_PLATELEGS(44),
	RUNE_SHIELD(45),
	
	RAW_CHICKEN(49),
	COOKED_CHICKEN(50),
	
	
	// TODO add the restof the items
	
	COPPER_SWORD(11),
	COPPER_DAGGERS(12),
	COPPER_HAMMER(13),
	
	
	IRON_SWORD(18),
	IRON_DAGGERS(19),
	IRON_HAMMER(20),
	
	STEEL_SWORD(25),
	STEEL_DAGGERS(26),
	STEEL_HAMMER(27),
	
	MITHRIL_SWORD(32),
	MITHRIL_DAGGERS(33),
	MITHRIL_HAMMER(34),
	
	ADDY_SWORD(39),
	ADDY_DAGGERS(40),
	ADDY_HAMMER(41),
	
	RUNE_SWORD(46),
	RUNE_DAGGERS(47),
	RUNE_HAMMER(48),
	
	COPPER_SWORD_II(51),
	COPPER_DAGGERS_II(58),
	COPPER_HAMMER_II(65),
	IRON_SWORD_II(72),
	IRON_DAGGERS_II(79),
	IRON_HAMMER_II(86),
	STEEL_SWORD_II(93),
	STEEL_DAGGERS_II(100),
	STEEL_HAMMER_II(107),
	MITHRIL_SWORD_II(114),
	MITHRIL_DAGGERS_II(121),
	MITHRIL_HAMMER_II(128),
	ADDY_SWORD_II(135),
	ADDY_DAGGERS_II(142),
	ADDY_HAMMER_II(149),
	RUNE_SWORD_II(156),
	RUNE_DAGGERS_II(163),
	RUNE_HAMMER_II(170),

	COPPER_SWORD_III(52),
	COPPER_DAGGERS_III(59),
	COPPER_HAMMER_III(66),
	IRON_SWORD_III(73),
	IRON_DAGGERS_III(80),
	IRON_HAMMER_III(87),
	STEEL_SWORD_III(94),
	STEEL_DAGGERS_III(101),
	STEEL_HAMMER_III(108),
	MITHRIL_SWORD_III(115),
	MITHRIL_DAGGERS_III(122),
	MITHRIL_HAMMER_III(129),
	ADDY_SWORD_III(136),
	ADDY_DAGGERS_III(143),
	ADDY_HAMMER_III(150),
	RUNE_SWORD_III(157),
	RUNE_DAGGERS_III(164),
	RUNE_HAMMER_III(171),

	COPPER_SWORD_IV(53),
	COPPER_DAGGERS_IV(60),
	COPPER_HAMMER_IV(67),
	IRON_SWORD_IV(74),
	IRON_DAGGERS_IV(81),
	IRON_HAMMER_IV(88),
	STEEL_SWORD_IV(95),
	STEEL_DAGGERS_IV(102),
	STEEL_HAMMER_IV(109),
	MITHRIL_SWORD_IV(116),
	MITHRIL_DAGGERS_IV(123),
	MITHRIL_HAMMER_IV(130),
	ADDY_SWORD_IV(137),
	ADDY_DAGGERS_IV(144),
	ADDY_HAMMER_IV(151),
	RUNE_SWORD_IV(158),
	RUNE_DAGGERS_IV(165),
	RUNE_HAMMER_IV(172),

	COPPER_SWORD_V(54),
	COPPER_DAGGERS_V(61),
	COPPER_HAMMER_V(68),
	IRON_SWORD_V(75),
	IRON_DAGGERS_V(82),
	IRON_HAMMER_V(89),
	STEEL_SWORD_V(96),
	STEEL_DAGGERS_V(103),
	STEEL_HAMMER_V(110),
	MITHRIL_SWORD_V(117),
	MITHRIL_DAGGERS_V(124),
	MITHRIL_HAMMER_V(131),
	ADDY_SWORD_V(138),
	ADDY_DAGGERS_V(145),
	ADDY_HAMMER_V(152),
	RUNE_SWORD_V(159),
	RUNE_DAGGERS_V(166),
	RUNE_HAMMER_V(173),

	COPPER_SWORD_VI(55),
	COPPER_DAGGERS_VI(62),
	COPPER_HAMMER_VI(69),
	IRON_SWORD_VI(76),
	IRON_DAGGERS_VI(83),
	IRON_HAMMER_VI(90),
	STEEL_SWORD_VI(97),
	STEEL_DAGGERS_VI(104),
	STEEL_HAMMER_VI(111),
	MITHRIL_SWORD_VI(118),
	MITHRIL_DAGGERS_VI(125),
	MITHRIL_HAMMER_VI(132),
	ADDY_SWORD_VI(139),
	ADDY_DAGGERS_VI(146),
	ADDY_HAMMER_VI(153),
	RUNE_SWORD_VI(160),
	RUNE_DAGGERS_VI(167),
	RUNE_HAMMER_VI(174),

	COPPER_SWORD_VII(56),
	COPPER_DAGGERS_VII(63),
	COPPER_HAMMER_VII(70),
	IRON_SWORD_VII(77),
	IRON_DAGGERS_VII(84),
	IRON_HAMMER_VII(91),
	STEEL_SWORD_VII(98),
	STEEL_DAGGERS_VII(105),
	STEEL_HAMMER_VII(112),
	MITHRIL_SWORD_VII(119),
	MITHRIL_DAGGERS_VII(126),
	MITHRIL_HAMMER_VII(133),
	ADDY_SWORD_VII(140),
	ADDY_DAGGERS_VII(147),
	ADDY_HAMMER_VII(154),
	RUNE_SWORD_VII(161),
	RUNE_DAGGERS_VII(168),
	RUNE_HAMMER_VII(175),

	COPPER_SWORD_VIII(57),
	COPPER_DAGGERS_VIII(64),
	COPPER_HAMMER_VIII(71),
	IRON_SWORD_VIII(78),
	IRON_DAGGERS_VIII(85),
	IRON_HAMMER_VIII(92),
	STEEL_SWORD_VIII(99),
	STEEL_DAGGERS_VIII(106),
	STEEL_HAMMER_VIII(113),
	MITHRIL_SWORD_VIII(120),
	MITHRIL_DAGGERS_VIII(127),
	MITHRIL_HAMMER_VIII(134),
	ADDY_SWORD_VIII(141),
	ADDY_DAGGERS_VIII(148),
	ADDY_HAMMER_VIII(155),
	RUNE_SWORD_VIII(162),
	RUNE_DAGGERS_VIII(169),
	RUNE_HAMMER_VIII(176),
	
	STEVES_BABY(177),
	COINS(178),
	
	ADAMANTITE_ORE(179),
	RUNITE_ORE(180),
	
	WIZARD_HAT(181),
	WIZARD_ROBE(182),
	
	PICKAXE(183),
	GOLDEN_PICKAXE(184),
	MAGIC_PICKAXE(185),
	MAGIC_GOLDEN_PICKAXE(186),
	
	COPPER_REINFORCEMENT(187),
	IRON_REINFORCEMENT(188),
	STEEL_REINFORCEMENT(189),
	MITHRIL_REINFORCEMENT(190),
	ADDY_REINFORCEMENT(191),
	RUNE_REINFORCEMENT(192),
	
	REINFORCED_COPPER_HELMET(193),
	REINFORCED_COPPER_PLATEBODY(194),
	REINFORCED_COPPER_PLATELEGS(195),
	REINFORCED_COPPER_SHIELD(196),
	
	REINFORCED_IRON_HELMET(197),
	REINFORCED_IRON_PLATEBODY(198),
	REINFORCED_IRON_PLATELEGS(199),
	REINFORCED_IRON_SHIELD(200),
	
	REINFORCED_STEEL_HELMET(201),
	REINFORCED_STEEL_PLATEBODY(202),
	REINFORCED_STEEL_PLATELEGS(203),
	REINFORCED_STEEL_SHIELD(204),
	
	REINFORCED_MITHRIL_HELMET(205),
	REINFORCED_MITHRIL_PLATEBODY(206),
	REINFORCED_MITHRIL_PLATELEGS(207),
	REINFORCED_MITHRIL_SHIELD(208),
	
	REINFORCED_ADDY_HELMET(209),
	REINFORCED_ADDY_PLATEBODY(210),
	REINFORCED_ADDY_PLATELEGS(211),
	REINFORCED_ADDY_SHIELD(212),
	
	REINFORCED_RUNE_HELMET(213),
	REINFORCED_RUNE_PLATEBODY(214),
	REINFORCED_RUNE_PLATELEGS(215),
	REINFORCED_RUNE_SHIELD(216),
	
	RAW_GIANT_WOLF_LEG(217),
	GIANT_WOLF_LEG(218),
	
	DRAGON_SWORD(219),
	DRAGON_SWORD_II(220),
	DRAGON_SWORD_III(221),
	DRAGON_SWORD_IV(222),
	DRAGON_SWORD_V(223),
	DRAGON_SWORD_VI(224),
	DRAGON_SWORD_VII(225),
	DRAGON_SWORD_VIII(226),
	
	GOBLIN_STANK_4(227),
	GOBLIN_STANK_3(228),
	GOBLIN_STANK_2(229),
	GOBLIN_STANK_1(230),
	
	RESTORATION_POTION_4(231),
	RESTORATION_POTION_3(232),
	RESTORATION_POTION_2(233),
	RESTORATION_POTION_1(234),
	
	ACCURACY_POTION_4(235),
	ACCURACY_POTION_3(236),
	ACCURACY_POTION_2(237),
	ACCURACY_POTION_1(238),
	
	DAMAGE_POTION_4(239),
	DAMAGE_POTION_3(240),
	DAMAGE_POTION_2(241),
	DAMAGE_POTION_1(242),
	
	AGILITY_POTION_4(243),
	AGILITY_POTION_3(244),
	AGILITY_POTION_2(245),
	AGILITY_POTION_1(246),
	
	DEFENCE_POTION_4(247),
	DEFENCE_POTION_3(248),
	DEFENCE_POTION_2(249),
	DEFENCE_POTION_1(250),
	
	POISON_4(251),
	POISON_3(252),
	POISON_2(253),
	POISON_1(254),
	
	ANTIPOISON_4(255),
	ANTIPOISON_3(256),
	ANTIPOISON_2(257),
	ANTIPOISON_1(258),
	
	MAGIC_POTION_4(259),
	MAGIC_POTION_3(260),
	MAGIC_POTION_2(261),
	MAGIC_POTION_1(262),
	
	BUTTERFLY(263),
	RABBIT_HEART(264),
	FROG_LEGS(265),
	RAT_TAIL(266),
	
	ORANGE_HARNIA(267),
	SKY_FLOWER(268),
	RED_RUSSINE(269),
	DARK_BLUEBELL(270),
	STARFLOWER(271),
	
	VIAL(272),
	ORANGE_HARNIA_MIX(273),
	SKY_FLOWER_MIX(274),
	RED_RUSSINE_MIX(275),
	DARK_BLUEBELL_MIX(276),
	STARFLOWER_MIX(277),
	
	GOBLIN_NAILS(278),
	WOLF_TEETH(279),
	
	CABBAGE(280),
	
	WHIRLWIND_RUNE(281),
	TORNADO_RUNE(282),
	FIREWIND_RUNE(283),
	FIRE_TORNADO_RUNE(284),
	DISEASE_RUNE(285),
	DECAY_RUNE(286),
	BLOOD_TITHE_RUNE(287),
	TYROTOWN_TELEPORT_RUNE(288),
	
	RAW_SHRIMPS(307),
	SHRIMPS(308),
	BURNT_SHRIMPS(309),
	
	RAW_OCTOPUS(310),
	OCTOPUS(311),
	BURNT_OCTOPUS(312);

	@Getter private int value;
	Items(int value) {
		this.value = value;
	}
	
	public static Items withValue(final int val) {
		for (Items item : Items.values()) {
			if (item.getValue() == val)
				return item;
		}
		return null;
	}
}
