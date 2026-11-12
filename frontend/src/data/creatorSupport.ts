import type { SectionDefinition } from "../types";
import { OFFICIAL_DOCS_URL } from "../utils";

export type SupportResourceKind = "公式ガイド" | "公式テンプレート" | "レクチャー" | "素材";

export interface SupportResource {
  title: string;
  description: string;
  url: string;
  source: string;
  kind: SupportResourceKind;
}

export interface SectionSupport {
  headline: string;
  summary: string;
  steps: string[];
  prompts: string[];
  resources: SupportResource[];
}

const TEMPLATE_BASIC: SupportResource = {
  title: "基本テンプレート",
  description: "空欄を埋めるだけで、基本構成の作品を組み立てられる公式テンプレート。",
  url: "https://docs.studio.uzu-app.com/overview/template/basic",
  source: "UZU STUDIO",
  kind: "公式テンプレート",
};

const TEMPLATE_ADVANCED: SupportResource = {
  title: "発展テンプレート",
  description: "種別や条件付きアイコンなど、発展機能を使った構成を確認できる公式テンプレート。",
  url: "https://docs.studio.uzu-app.com/overview/template/advanced",
  source: "UZU STUDIO",
  kind: "公式テンプレート",
};

const CHARACTER_ICON: SupportResource = {
  title: "キャラクターアイコン素材",
  description: "UZU作品のPC・NPCアイコンに使える無料素材。利用条件と加工ルールを確認して使う。",
  url: "https://docs.studio.uzu-app.com/resource/character-icon",
  source: "UZU STUDIO",
  kind: "素材",
};

const NOTE_LECTURE: SupportResource = {
  title: "一晩で完成させるマダミス制作講座",
  description: "プロット0からテストプレイ可能まで、制作の順番とタイムテーブルを学べるUZU公式の外部レクチャー。",
  url: "https://note.com/uzuapp/n/nf6f8e467d016",
  source: "UZU公式note",
  kind: "レクチャー",
};

const MURDER_JP_LECTURE: SupportResource = {
  title: "作家向けマダミス講座",
  description: "過去に開催された作家向け講座の紹介。キャラクターづくりやテストプレイの実践知を確認できる。",
  url: "https://www.murder.jp/product/20250512-1500.html",
  source: "Murder.JP",
  kind: "レクチャー",
};

const VISUAL_REGULATION: SupportResource = {
  title: "キービジュアル作成レギュレーション",
  description: "16:9、コントラスト、160×90でも読めるタイトル、タイトル全文、素材の権利を確認する公式ルール。",
  url: "https://docs.studio.uzu-app.com/overview/visual-regulation",
  source: "UZU STUDIO",
  kind: "公式ガイド",
};

const IMAGE_SIZE: SupportResource = {
  title: "画像サイズ一覧",
  description: "キービジュアルやサブ画像など、推奨サイズをまとめた公式ページ。",
  url: "https://docs.studio.uzu-app.com/others/image-px",
  source: "UZU STUDIO",
  kind: "公式ガイド",
};

const COMMON_MISTAKE: SupportResource = {
  title: "公開前のよくある指摘",
  description: "補助動詞、「こと」「とき」、記号、句読点、字下げ、表記揺れの指摘を確認できる。",
  url: "https://docs.studio.uzu-app.com/overview/makingflow/submit/common-mistake",
  source: "UZU STUDIO",
  kind: "公式ガイド",
};

const QUESTIONNAIRE: SupportResource = {
  title: "テストプレイアンケート",
  description: "テストプレイで集めたい項目と、提出に使うアンケートの公式案内。",
  url: "https://docs.studio.uzu-app.com/overview/makingflow/submit/questionnaire",
  source: "UZU STUDIO",
  kind: "公式ガイド",
};

const CHARACTER_SUPPORT: SectionSupport = {
  headline: "名前より先に、関係性と秘密を決める",
  summary:
    "見た目から入ると、全員が独立した説明書になりがちです。まず誰と誰が何を隠しているかを決めると、配布テキスト・手がかり・会話のきっかけが自然につながります。",
  steps: [
    "全員に「表向きの目的」「隠したいこと」「最後に言いたい一言」を1つずつ書く。",
    "人物を線でつなぎ、「信頼」「疑い」「片思い」などの向きを決める。",
    "その関係が表に出るフェーズを1つ決め、配布テキストと手がかりに反映する。",
  ],
  prompts: [
    "この人物は、誰に知られたくない何を抱えている？",
    "この人物が誤解していることは？",
    "犯人だと思われる理由は？（冤罪でも成立する？）",
  ],
  resources: [CHARACTER_ICON, TEMPLATE_BASIC, NOTE_LECTURE, MURDER_JP_LECTURE],
};

const NPC_SUPPORT: SectionSupport = {
  headline: "NPCは「情報の運び役」として設計する",
  summary:
    "NPCを増やすほど世界は豊かになりますが、表示する情報も増えます。まずは役割を1行で決め、一覧に出すか伏せるかを決めると、ネタバレ事故を減らせます。",
  steps: [
    "役割を「案内」「証言」「攪乱」から1つ選ぶ。",
    "登場人物一覧に出すか、秘密NPCとして伏せるかを決める。",
    "誰のセリフとして、どのフェーズで登場するかを紐づける。",
  ],
  prompts: [
    "このNPCがいないと困る情報は？",
    "このNPCは嘘をつく？ それとも誤解している？",
    "一覧に出すと、何がネタバレになる？",
  ],
  resources: [CHARACTER_ICON, TEMPLATE_ADVANCED, NOTE_LECTURE],
};

const CREATE_SUPPORT: SectionSupport = {
  headline: "最初に「遊びの約束」を決める",
  summary:
    "作品登録は事務作業に見えますが、プレイヤーに約束する体験を決める場所です。誰と、どれくらいの時間で、どんな気持ちになって帰るのかを先に言語化しましょう。",
  steps: [
    "想定プレイヤーを1人、具体的に想像する。",
    "開始時刻から10時間の作業枠を逆算し、テストプレイ・キービジュアル・キャラクターイラストは別枠で確保する。",
    "タイトル・あらすじ・注意事項が同じ約束を指しているか確認する。",
  ],
  prompts: [
    "遊び終えた人に、最初に言ってほしい言葉は？",
    "この作品が苦手な人は、どんな人？",
    "タイトルだけで、体験の種類が伝わる？",
  ],
  resources: [TEMPLATE_BASIC, TEMPLATE_ADVANCED, NOTE_LECTURE],
};

const SUMMARY_SUPPORT: SectionSupport = {
  headline: "あらすじは「事件の入口」だけを見せる",
  summary:
    "あらすじで核心まで説明すると、推理の楽しみが減ります。誰が、どこで、何を失い、何を決めるのか。プレイヤーが最初に知るべき情報だけを残しましょう。",
  steps: [
    "プロット0で事件・犯人・動機・トリック・配役を1枚にまとめる。",
    "事件の発端を3文以内で書き、プレイヤーが最初に抱く疑問を1つ決める。",
    "キービジュアルと注意事項が、あらすじの印象と矛盾しないか確認する。",
  ],
  prompts: [
    "最初に「えっ」と思わせる情報は何？",
    "まだ伏せるべき真相は何？",
    "注意事項を読んだ人が、安心して参加できる？",
  ],
  resources: [VISUAL_REGULATION, IMAGE_SIZE, NOTE_LECTURE],
};

const PHASES_SUPPORT: SectionSupport = {
  headline: "フェーズは「感情のリズム」で並べる",
  summary:
    "情報を出す順番が、推理と会話の熱量を決めます。導入・調査・議論・決断の4拍子を意識し、各フェーズに新しい発見を1つ入れると、退屈な時間を減らせます。",
  steps: [
    "フェーズを「導入」「調査」「議論」「決断」の4拍子で並べる。",
    "各フェーズに「新しく分かること」を1つ以上入れる。",
    "投票の前に、判断材料が揃うか通しで確認する。",
  ],
  prompts: [
    "このフェーズが終わったとき、誰の疑いが動く？",
    "ここで初めて出る情報は何？",
    "話す時間と調べる時間のバランスは？",
  ],
  resources: [TEMPLATE_BASIC, NOTE_LECTURE, MURDER_JP_LECTURE],
};

const FLOW_SUPPORT: SectionSupport = {
  headline: "分岐は「選択の重み」を見せる",
  summary:
    "分岐は数ではなく、選択の結果が伝わるかが大切です。プレイヤーが何を決め、その結果として誰がどう変わるのかを地図にしてから設定しましょう。",
  steps: [
    "分岐点を3つまでに絞り、それぞれの選択を言葉にする。",
    "未達成や失敗の受け皿を必ず用意する。",
    "エンディングで、選択がどこに回収されるかを確認する。",
  ],
  prompts: [
    "この選択で、誰の印象が変わる？",
    "失敗しても物語が止まらない？",
    "全分岐を紙に書き出した？",
  ],
  resources: [TEMPLATE_ADVANCED, NOTE_LECTURE],
};

const TEXTS_SUPPORT: SectionSupport = {
  headline: "説明より「事実・感情・推測」を分ける",
  summary:
    "長い説明を足すより、情報の種類を分ける方が読みやすくなります。事実、感情、推測を書き分けると、プレイヤーが誤読する余地も設計できます。",
  steps: [
    "本文の一文を「事実」「感情」「推測」に色分けする。",
    "補助動詞、「こと」「とき」、ダッシュ、三点リーダー、！？後のスペース、字下げ、表記揺れを確認する。",
    "スマホ幅で通読し、長文が詰まらないか確認する。",
  ],
  prompts: [
    "この文章は、誰の主観？",
    "公式の「文章のよくある指摘」に照らして直す場所は？",
    "同じ情報を別の言い方で伝えている？",
  ],
  resources: [COMMON_MISTAKE, NOTE_LECTURE],
};

const CLUES_SUPPORT: SectionSupport = {
  headline: "手がかりは「次の問い」を生む",
  summary:
    "手がかりは真相の断片ではなく、プレイヤーの問いを進める道具です。1枚で分かることが増えすぎると、推理よりも作業になります。",
  steps: [
    "1つの手がかりに「事実」を1つだけ入れる。",
    "誤読できる余地を1つ残す。",
    "誰が、どのフェーズで取得できるかを決める。",
  ],
  prompts: [
    "この手がかりで、次に誰へ聞きたくなる？",
    "別の解釈は成立する？",
    "取得者が限定されすぎていない？",
  ],
  resources: [TEMPLATE_BASIC, NOTE_LECTURE],
};

const DEMO_SUPPORT: SectionSupport = {
  headline: "通しでなく「分岐の地図」をたどる",
  summary:
    "動作確認は最後に1回通すだけでは足りません。公式が挙げるセリフ、配布先・条件、密談、投票分岐、BGM/SEの5点を、変更した場所とその周辺から確認します。",
  steps: [
    "開始位置ごとに、最初の画面と目的を確認する。",
    "セリフ、配布先・条件、密談、投票分岐、BGM/SEの順に分岐をたどる。",
    "エラー、権限、表示崩れをチェックリスト化する。",
  ],
  prompts: [
    "前回の修正で影響する分岐はどこ？",
    "途中参加や再開で破綻しない？",
    "スマホでも同じように進める？",
  ],
  resources: [COMMON_MISTAKE, NOTE_LECTURE],
};

const PLAYTEST_SUPPORT: SectionSupport = {
  headline: "感想ではなく「詰まり」を集める",
  summary:
    "有料公開ならUZU上1回以上を含む合計3回以上、アンケート回収が前提です。感想より、どこで迷い、どこで話が止まったかを集め、修正後に再テストします。",
  steps: [
    "UZU上1回以上を含む3回以上のテストプレイを計画する。",
    "公式アンケートで理解・楽しさ・迷いを回収し、初期項目は削除しない。",
    "修正候補を「致命」「体験」「好み」に分け、修正後に再確認する。",
  ],
  prompts: [
    "どこで説明を読み返していた？",
    "誰が一番話せていなかった？",
    "修正しないと公開できない問題は？",
  ],
  resources: [QUESTIONNAIRE, MURDER_JP_LECTURE, NOTE_LECTURE],
};

const REFLECTION_SUPPORT: SectionSupport = {
  headline: "真相は「手がかりの再編集」として見せる",
  summary:
    "感想戦は種明かしの時間ですが、プレイヤーが納得するかは、真相と手がかりの対応が見えるかにかかっています。推理の道筋をなぞれる構成にしましょう。",
  steps: [
    "手がかりと推論の対応を一覧にする。",
    "ミッション達成と得点の表示を確認する。",
    "同票や特殊条件の結果表示を確認する。",
  ],
  prompts: [
    "どの手がかりから、その真相に届く？",
    "ミッションは感想戦で回収できる？",
    "納得できない人がいそうな箇所は？",
  ],
  resources: [TEMPLATE_BASIC, NOTE_LECTURE],
};

const SUPPORT_BY_SECTION: Record<string, SectionSupport> = {
  CREATE: CREATE_SUPPORT,
  SUMMARY: SUMMARY_SUPPORT,
  CHARACTERS: CHARACTER_SUPPORT,
  NPC: NPC_SUPPORT,
  PHASES: PHASES_SUPPORT,
  FLOW: FLOW_SUPPORT,
  TEXTS: TEXTS_SUPPORT,
  CLUES: CLUES_SUPPORT,
  DEMO: DEMO_SUPPORT,
  PLAYTEST: PLAYTEST_SUPPORT,
  REFLECTION: REFLECTION_SUPPORT,
};

function officialResource(section: SectionDefinition): SupportResource {
  return {
    title: `${section.label}の公式ガイド`,
    description: section.purpose,
    url: `${OFFICIAL_DOCS_URL}${section.uzuPath}`,
    source: "UZU STUDIO",
    kind: "公式ガイド",
  };
}

function fallbackSupport(section: SectionDefinition): SectionSupport {
  return {
    headline: `${section.label}で「まず決めること」を1つに絞る`,
    summary:
      section.purpose ||
      "公式ガイドで画面の目的を確認し、今日決めることを1つに絞ると進めやすくなります。",
    steps: [
      "公式ガイドで、この画面の目的を確認する。",
      `決めることを1つ選ぶ（例: ${section.fields[0] ?? section.label}）。`,
      "決めた内容をチェック項目かメモに残す。",
    ],
    prompts:
      section.tips.length > 0
        ? section.tips
        : ["この画面で、今いちばん迷っていることは？"],
    resources: [officialResource(section), TEMPLATE_BASIC, NOTE_LECTURE],
  };
}

export function supportForSection(section: SectionDefinition): SectionSupport {
  const support = SUPPORT_BY_SECTION[section.key];
  if (!support) return fallbackSupport(section);
  return {
    ...support,
    resources: [officialResource(section), ...support.resources],
  };
}
