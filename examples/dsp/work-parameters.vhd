library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

library ieee_proposed;
use ieee_proposed.fixed_pkg.all;

library work;
use work.the_library.all;

entity parameters is
  generic (freq_len : integer := 24;
           param_len : integer := 16);
  port (rst : in std_logic;
        clk_my : in std_logic;
        rot_a : in std_logic;
        rot_b : in std_logic;
        btn_minus : in std_logic;
        btn_plus : in std_logic;
        mode : in std_logic_vector(2 downto 0);
        freq : out unsigned(freq_len - 1 downto 0);
        params : out air2_params;
        rs_tx_pin : out std_logic;
        fx2_io : out std_logic_vector(40 downto 39));
end;

architecture behavioral of parameters is
  constant word_len : integer := 32;
  signal change : signed(1 downto 0);
  signal change_freq : signed(1 downto 0);
  type changes_t is array(air2_params'range) of signed(1 downto 0);
  signal changes : changes_t;
  signal signs : std_logic_vector(air2_params'range);
  signal raw_freq : unsigned(freq_len - 1 downto 0);
  type raws_t is array(air2_params'range) of unsigned(param_len - 1 downto 0);
  signal raws : raws_t;
  signal prevals : air2_params;
  signal vals : air2_params;
  constant rs_prefix : std_logic_vector(word_len - 1 downto 0) :=
  "01000001" & "01010100" & "01000001" & "01000100";
  signal rs_seq : std_logic_vector(air2_params'length * word_len - 1 downto 0);
begin
  d1 : entity work.rotary_decoder
  port map(rst, clk_my, rot_a, rot_b, change);

  k0 : change_freq <= change when mode = "000" else "00";
  k1 : entity work.decimal_acc
  generic map(freq_len, 7)
  port map(rst, clk_my, change_freq, raw_freq);

  l0 : process (rst, clk_my)
    variable index : integer;
  begin
    if rst = '1' then
      signs <= (others => '0');
    elsif clk_my'event and clk_my = '1' then
      index := to_integer(unsigned(mode(1 downto 0)));
      if btn_minus = '1' then
        signs(index) <= '1';
      elsif btn_plus = '1' then
        signs(index) <= '0';
      end if;
    end if;
  end process;

  t0 : for i in air2_params'range generate
    changes(i) <= change when mode = std_logic_vector(to_unsigned(i + 4, 3)) else "00";
    t00 : entity work.decimal_acc
    generic map(param_len, 5)
    port map(rst, clk_my, changes(i), raws(i));
    prevals(i) <= to_sfixed(std_logic_vector(raws(i)), param_len - 1, 0);
    vals(i) <= resize(- prevals(i), param_len - 1, 0) when signs(i) = '1' else prevals(i);
    rs_seq((i + 1) * word_len - 1 downto i * word_len) <= as32slv(vals(i));
    params(i) <= vals(i);
  end generate;

  r1 : entity work.rs232tx
  generic map((air2_params'length + 2) * word_len)
  port map(rst, clk_my, rs_prefix & rs_seq & as32slv(raw_freq), rs_tx_pin, fx2_io);

  w0 : freq <= raw_freq;
end;

